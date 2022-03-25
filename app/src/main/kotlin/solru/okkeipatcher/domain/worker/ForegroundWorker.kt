package solru.okkeipatcher.domain.worker

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.base.Observable
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.util.extension.empty
import solru.okkeipatcher.util.extension.putParcelable
import solru.okkeipatcher.util.extension.putSerializable
import java.util.concurrent.atomic.AtomicInteger

private val workerProgressNotificationId = AtomicInteger(813047)
private val workerMessageNotificationId = AtomicInteger(49725)

abstract class ForegroundWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val service: Observable
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

	protected abstract suspend fun doServiceWork()
	protected abstract fun createPendingIntent(): PendingIntent

	final override suspend fun doWork(): Result {
		try {
			setForeground(createForegroundInfo())
			coroutineScope {
				val observeJob = observeService()
				doServiceWork()
				observeJob.cancel()
			}
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
			return Result.failure(
				Data.Builder()
					.putSerializable(KEY_FAILURE_CAUSE, t)
					.build()
			)
		}
		clearShownMessageNotifications()
		val successMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_success)
		)
		displayMessageNotification(successMessage)
		return Result.success()
	}

	private fun createForegroundInfo() = ForegroundInfo(progressNotificationId, progressNotificationBuilder.build())

	private fun CoroutineScope.observeService() = launch {
		reportProgress()
		updateNotificationProgress()
		updateNotificationStatus()
		collectWarnings()
	}

	private fun CoroutineScope.reportProgress() = launch {
		combine(
			service.status,
			service.progress
		) { status, progressData -> status to progressData }
			.conflate()
			.collect {
				val (status, progressData) = it
				setProgress(
					Data.Builder()
						.putSerializable(KEY_STATUS, status)
						.putParcelable(KEY_PROGRESS_DATA, progressData)
						.build()
				)
			}
	}

	private fun CoroutineScope.updateNotificationProgress() = launch {
		service.progress
			.conflate()
			.collect {
				val notification = progressNotificationBuilder.setProgress(
					it.max,
					it.progress,
					it.isIndeterminate
				).build()
				notificationManager?.notify(progressNotificationId, notification)
				delay(1000)
			}
	}

	private fun CoroutineScope.updateNotificationStatus() = launch {
		service.status
			.conflate()
			.collect {
				val statusString = it.resolve(applicationContext)
				val notification = progressNotificationBuilder.setContentText(statusString).build()
				notificationManager?.notify(progressNotificationId, notification)
				delay(500)
			}
	}

	private fun CoroutineScope.collectWarnings() = launch {
		service.messages.collect {
			displayMessageNotification(it)
		}
	}

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
			setContentText(String.empty)
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