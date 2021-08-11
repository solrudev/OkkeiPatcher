package solru.okkeipatcher.core.workers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppService
import solru.okkeipatcher.ui.activities.MainActivity
import solru.okkeipatcher.utils.extensions.empty

private const val PROGRESS_NOTIFICATION_ID = 813047

abstract class BaseWorker(
	context: Context,
	workerParameters: WorkerParameters,
	notificationTitleId: Int,
	private val service: AppService
) : CoroutineWorker(context, workerParameters) {

	private val notificationManager =
		context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	private val channelId = applicationContext.getString(R.string.notification_channel_id)

	private val progressNotificationBuilder =
		createNotificationBuilder(notificationTitleId, progressNotification = true)

	private val simpleNotificationBuilder =
		createNotificationBuilder(progressNotification = false)

	abstract suspend fun doServiceWork()

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
			// TODO propagate error to view
			if (e !is CancellationException) {
				Log.e(this@BaseWorker::class.qualifiedName, "", e)
				return Result.failure(workDataOf(KEY_FAILURE_CAUSE to e.message))
			}
		}
		return Result.success()
	}

	private fun createForegroundInfo(): ForegroundInfo {
		return ForegroundInfo(PROGRESS_NOTIFICATION_ID, progressNotificationBuilder.build())
	}

	private fun createNotificationBuilder(
		titleId: Int = R.string.empty,
		progressNotification: Boolean
	): NotificationCompat.Builder {
		val title = applicationContext.getString(titleId)
		val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val contentIntent = PendingIntent.getActivity(
			applicationContext,
			MESSAGE_NOTIFICATION_ID,
			activityIntent,
			PendingIntent.FLAG_UPDATE_CURRENT
		)
		return NotificationCompat.Builder(applicationContext, channelId).apply {
			setContentTitle(title)
			setContentText(String.empty)
			priority = NotificationCompat.PRIORITY_DEFAULT
			setSmallIcon(R.mipmap.ic_launcher_foreground)
			setContentIntent(contentIntent)
			setSound(null)
			if (progressNotification) {
				val abort = applicationContext.getString(R.string.abort)
				val cancelIntent =
					WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
				addAction(android.R.drawable.ic_delete, abort, cancelIntent)
				setProgress(100, 0, false)
				setOnlyAlertOnce(true)
			} else {
				setAutoCancel(true)
			}
		}
	}

	private fun createMessageNotification(titleId: Int, messageId: Int): Notification {
		val title = applicationContext.getString(titleId)
		val message = applicationContext.getString(messageId)
		return simpleNotificationBuilder.apply {
			setContentTitle(title)
			setContentText(message)
		}.build()
	}

	private fun CoroutineScope.observeService() = launch {
		launch {
			combine(
				service.progress,
				service.status
			) { progressData, status -> progressData to status }
				.conflate()
				.collect {
					setProgress(
						workDataOf(
							KEY_PROGRESS to it.first.progress,
							KEY_PROGRESS_MAX to it.first.max,
							KEY_IS_PROGRESS_INDETERMINATE to it.first.isIndeterminate,
							KEY_STATUS to it.second
						)
					)
				}
		}
		launch {
			service.progress.conflate()
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
		launch {
			service.status.conflate()
				.collect {
					val statusString = applicationContext.getString(it)
					val notification =
						progressNotificationBuilder.setContentText(statusString).build()
					notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
					delay(500)
				}
		}
		launch {
			service.message.collect {
				++MESSAGE_NOTIFICATION_ID
				val notification = createMessageNotification(it.titleId, it.messageId)
				notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification)
			}
		}
	}

	companion object {
		const val KEY_PROGRESS = "PROGRESS"
		const val KEY_PROGRESS_MAX = "PROGRESS_MAX"
		const val KEY_IS_PROGRESS_INDETERMINATE = "IS_PROGRESS_INDETERMINATE"
		const val KEY_STATUS = "STATUS"
		const val KEY_FAILURE_CAUSE = "WORKER_FAILURE_CAUSE"

		@JvmStatic
		private var MESSAGE_NOTIFICATION_ID = 49725
	}
}