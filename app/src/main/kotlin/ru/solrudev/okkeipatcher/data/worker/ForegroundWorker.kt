package ru.solrudev.okkeipatcher.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.data.worker.model.WorkerFailure
import ru.solrudev.okkeipatcher.data.worker.util.setProgress
import ru.solrudev.okkeipatcher.data.worker.util.workerFailure
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.service.operation.factory.OperationFactory
import ru.solrudev.okkeipatcher.ui.host.HostActivity
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import ru.solrudev.okkeipatcher.domain.core.Result as DomainResult

private val workerProgressNotificationId = AtomicInteger(813047)
private val workerMessageNotificationId = AtomicInteger(49725)

abstract class ForegroundWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val workManager: WorkManager,
	private val operationFactory: OperationFactory<DomainResult>,
	private val workLabel: LocalizedString
) : CoroutineWorker(context, workerParameters) {

	private val notificationManager = context.getSystemService<NotificationManager>()
	private val progressNotificationId = workerProgressNotificationId.incrementAndGet()
	private val shownMessageNotifications = mutableListOf<Int>()
	private val shownMessageNotificationsMutex = Mutex()

	private val simpleNotificationBuilder by lazy {
		createNotificationBuilder(progressNotification = false)
	}

	private val progressNotificationBuilder by lazy {
		createNotificationBuilder(workLabel, progressNotification = true)
	}

	final override suspend fun doWork() = try {
		if (runAttemptCount > 0) {
			workManager
				.cancelWorkById(id)
				.await()
		}
		setForeground(createForegroundInfo())
		val operation = operationFactory.create()
		operation
			.canInvoke()
			.onFailure {
				return createFailure(
					WorkerFailure.Domain(it.reason)
				)
			}
		val result: DomainResult
		coroutineScope {
			val observeJob = observeOperation(operation)
			result = operation()
			observeJob.cancel()
		}
		result.onFailure {
			return createFailure(
				WorkerFailure.Domain(it.reason)
			)
		}
		createSuccess()
	} catch (t: Throwable) {
		if (t is CancellationException) {
			throw t
		}
		createFailure(WorkerFailure.Unhandled(t))
	} finally {
		clearShownMessageNotifications()
		withContext(NonCancellable) {
			delay(250.milliseconds) // for foreground service notification to be canceled before returning
		}
	}

	private fun createForegroundInfo() = ForegroundInfo(progressNotificationId, progressNotificationBuilder.build())

	private suspend fun createSuccess(): Result {
		val successMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_success)
		)
		displayMessageNotification(successMessage, resultMessage = true)
		return Result.success()
	}

	private suspend fun createFailure(failure: WorkerFailure): Result {
		val failMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_failed)
		)
		displayMessageNotification(failMessage, resultMessage = true)
		return workerFailure(failure)
	}

	private fun CoroutineScope.observeOperation(operation: Operation<*>) = launch {
		reportProgress(operation)
		updateProgressNotification(operation)
		collectMessages(operation)
	}

	private fun CoroutineScope.reportProgress(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
			setProgress(status, progress, operation.progressMax)
		}
		.launchIn(this)

	private fun CoroutineScope.updateProgressNotification(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
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

	private suspend fun displayMessageNotification(message: Message, resultMessage: Boolean = false) =
		withContext(NonCancellable) {
			val titleString = message.title.resolve(applicationContext)
			val messageString = message.text.resolve(applicationContext)
			val notification = simpleNotificationBuilder.apply {
				setContentTitle(titleString)
				setContentText(messageString)
				if (messageString.length > 28) {
					setStyle(NotificationCompat.BigTextStyle().bigText(messageString))
				}
			}.build()
			val notificationId = workerMessageNotificationId.incrementAndGet()
			if (!resultMessage) {
				shownMessageNotificationsMutex.withLock {
					shownMessageNotifications.add(notificationId)
				}
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

	private fun createPendingIntent(): PendingIntent {
		val activityIntent = Intent(applicationContext, HostActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
		return PendingIntent.getActivity(
			applicationContext,
			0,
			activityIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or flagImmutable
		)
	}
}