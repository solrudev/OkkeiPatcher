package ru.solrudev.okkeipatcher.domain.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.util.extension.putParcelable
import ru.solrudev.okkeipatcher.domain.util.extension.putSerializable
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

private val workerProgressNotificationId = AtomicInteger(813047)
private val workerMessageNotificationId = AtomicInteger(49725)

abstract class ForegroundWorker(
	context: Context,
	workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

	protected abstract val progressNotificationTitle: LocalizedString
	private val notificationManager = context.getSystemService<NotificationManager>()
	private val progressNotificationId = workerProgressNotificationId.incrementAndGet()
	private val shownMessageNotifications = mutableListOf<Int>()
	private val shownMessageNotificationsMutex = Mutex()

	private val simpleNotificationBuilder by lazy {
		createNotificationBuilder(progressNotification = false)
	}

	private val progressNotificationBuilder by lazy {
		createNotificationBuilder(progressNotificationTitle, progressNotification = true)
	}

	protected abstract suspend fun getOperation(): Operation<*>
	protected abstract fun createPendingIntent(): PendingIntent

	final override suspend fun doWork() = try {
		setForeground(createForegroundInfo())
		coroutineScope {
			val operation = getOperation()
			val observeJob = observeOperation(operation)
			operation()
			observeJob.cancel()
		}
		clearShownMessageNotifications()
		val successMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_success)
		)
		displayMessageNotification(successMessage)
		Result.success()
	} catch (t: Throwable) {
		clearShownMessageNotifications()
		if (t is CancellationException) {
			throw t
		}
		val failMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_failed)
		)
		displayMessageNotification(failMessage)
		Result.failure(
			Data.Builder()
				.putSerializable(KEY_FAILURE_CAUSE, t)
				.build()
		)
	} finally {
		withContext(NonCancellable) {
			delay(250.milliseconds) // for foreground service notification to be canceled before returning
		}
	}

	private fun createForegroundInfo() = ForegroundInfo(progressNotificationId, progressNotificationBuilder.build())

	private fun CoroutineScope.observeOperation(operation: Operation<*>) = launch {
		reportProgress(operation)
		updateProgressNotification(operation)
		collectMessages(operation)
	}

	private fun CoroutineScope.reportProgress(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { pair ->
			val (status, progress) = pair
			val progressData = ProgressData(progress, operation.progressMax)
			setProgress(
				Data.Builder()
					.putSerializable(KEY_STATUS, status)
					.putParcelable(KEY_PROGRESS_DATA, progressData)
					.build()
			)
		}
		.launchIn(this)

	private fun CoroutineScope.updateProgressNotification(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { pair ->
			val (status, progress) = pair
			val statusString = status.resolve(applicationContext)
			val notification = progressNotificationBuilder
				.setContentText(statusString)
				.setProgress(operation.progressMax, progress, false)
				.build()
			notificationManager?.notify(progressNotificationId, notification)
			delay(500.milliseconds)
		}
		.launchIn(this)

	private fun CoroutineScope.collectMessages(operation: Operation<*>) = operation.messages
		.onEach { displayMessageNotification(it) }
		.launchIn(this)

	private suspend fun displayMessageNotification(message: Message) = withContext(NonCancellable) {
		val titleString = message.title.resolve(applicationContext)
		val messageString = message.message.resolve(applicationContext)
		val notification = simpleNotificationBuilder.apply {
			setContentTitle(titleString)
			setContentText(messageString)
			if (messageString.length > 28) {
				setStyle(NotificationCompat.BigTextStyle().bigText(messageString))
			}
		}.build()
		val notificationId = workerMessageNotificationId.incrementAndGet()
		shownMessageNotificationsMutex.withLock {
			shownMessageNotifications.add(notificationId)
		}
		notificationManager?.notify(notificationId, notification)
	}

	private suspend fun clearShownMessageNotifications() = withContext(NonCancellable) {
		shownMessageNotificationsMutex.withLock {
			shownMessageNotifications.forEach {
				notificationManager?.cancel(it)
			}
			shownMessageNotifications.clear()
		}
	}

	private fun createNotificationBuilder(
		title: LocalizedString = LocalizedString.empty(),
		progressNotification: Boolean
	): NotificationCompat.Builder {
		val contentTitle = title.resolve(applicationContext)
		val contentIntent = createPendingIntent()
		val channelId = if (progressNotification) {
			applicationContext.getString(R.string.notification_channel_progress_id)
		} else {
			applicationContext.getString(R.string.notification_channel_messages_id)
		}
		return NotificationCompat.Builder(applicationContext, channelId).apply {
			setContentTitle(contentTitle)
			setContentText("")
			priority = NotificationCompat.PRIORITY_DEFAULT
			setSmallIcon(R.mipmap.ic_launcher_foreground)
			setContentIntent(contentIntent)
			setSound(null)
			if (progressNotification) {
				setProgress(100, 0, false)
				setOnlyAlertOnce(true)
			} else {
				setAutoCancel(true)
			}
		}
	}

	companion object {
		const val KEY_PROGRESS_DATA = "PROGRESS_DATA"
		const val KEY_STATUS = "STATUS"
		const val KEY_FAILURE_CAUSE = "WORKER_FAILURE_CAUSE"
	}
}