package solru.okkeipatcher.core.workers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import solru.okkeipatcher.R
import solru.okkeipatcher.core.services.ObservableService
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.ui.activities.MainActivity
import solru.okkeipatcher.utils.extensions.empty
import solru.okkeipatcher.utils.extensions.putSerializable

private const val PROGRESS_NOTIFICATION_ID = 813047

abstract class BaseWorker(
	context: Context,
	workerParameters: WorkerParameters,
	notificationTitle: LocalizedString,
	private val service: ObservableService
) : CoroutineWorker(context, workerParameters) {

	private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	private val channelId = applicationContext.getString(R.string.notification_channel_id)
	private val simpleNotificationBuilder = createNotificationBuilder(progressNotification = false)

	private val progressNotificationBuilder =
		createNotificationBuilder(notificationTitle, progressNotification = true)

	abstract suspend fun doServiceWork()

	// TODO: notifications on finish
	final override suspend fun doWork(): Result {
		try {
			setForeground(createForegroundInfo())
			coroutineScope {
				observeService()
				doServiceWork()
				cancel()
			}
		} catch (e: Throwable) {
			notificationManager.cancelAll()
			if (e !is CancellationException) {
				return Result.failure(
					Data.Builder()
						.putSerializable(KEY_FAILURE_CAUSE, e)
						.build()
				)
			}
		}
		return Result.success()
	}

	private fun createForegroundInfo() = ForegroundInfo(PROGRESS_NOTIFICATION_ID, progressNotificationBuilder.build())

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
				setProgress(
					Data.Builder()
						.putSerializable(KEY_STATUS, it.first)
						.putSerializable(KEY_PROGRESS_DATA, it.second)
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
				notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
				delay(1000)
			}
	}

	private fun CoroutineScope.updateNotificationStatus() = launch {
		service.status
			.conflate()
			.collect {
				val statusString = it.resolve(applicationContext)
				val notification = progressNotificationBuilder.setContentText(statusString).build()
				notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
				delay(500)
			}
	}

	private fun CoroutineScope.collectWarnings() = launch {
		service.messages
			.collect {
				MESSAGE_NOTIFICATION_ID++
				val notification = createWarningNotification(it.title, it.message)
				notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification)
			}
	}

	private fun createWarningNotification(title: LocalizedString, message: LocalizedString): Notification {
		val titleString = title.resolve(applicationContext)
		val messageString = message.resolve(applicationContext)
		return simpleNotificationBuilder.apply {
			setContentTitle(titleString)
			setContentText(messageString)
		}.build()
	}

	private fun createNotificationBuilder(
		title: LocalizedString = LocalizedString.empty(),
		progressNotification: Boolean
	): NotificationCompat.Builder {
		val contentTitle = title.resolve(applicationContext)
		val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val contentIntent = PendingIntent.getActivity(
			applicationContext,
			MESSAGE_NOTIFICATION_ID,
			activityIntent,
			PendingIntent.FLAG_UPDATE_CURRENT.apply {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					this or PendingIntent.FLAG_IMMUTABLE
				}
			}
		)
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
		private var MESSAGE_NOTIFICATION_ID = 49725
	}
}