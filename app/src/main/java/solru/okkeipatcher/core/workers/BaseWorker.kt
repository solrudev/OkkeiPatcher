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
import kotlinx.serialization.decodeFromString
import solru.okkeipatcher.R
import solru.okkeipatcher.core.JsonSerializer
import solru.okkeipatcher.core.base.AppService
import solru.okkeipatcher.model.dto.AppServiceConfig
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.ui.activities.MainActivity
import solru.okkeipatcher.utils.extensions.empty

abstract class BaseWorker(
	context: Context,
	workerParameters: WorkerParameters,
	titleId: Int,
	private val service: AppService
) : CoroutineWorker(context, workerParameters) {

	protected lateinit var manifest: OkkeiManifest
	protected lateinit var config: AppServiceConfig

	private val notificationManager =
		context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	private val channelId = applicationContext.getString(R.string.notification_channel_id)

	private val progressNotificationBuilder =
		createNotificationBuilder(titleId, progressNotification = true)

	private val simpleNotificationBuilder =
		createNotificationBuilder(progressNotification = false)

	final override suspend fun doWork() = coroutineScope {
		try {
			deserializeInputData()
		} catch (e: Exception) {
			return@coroutineScope Result.failure(workDataOf(KEY_FAILURE_CAUSE to e.message))
		}
		try {
			setForeground(createForegroundInfo())
			coroutineScope {
				collectProgress()
				collectStatus()
				collectMessages()
				startService()
			}
		} catch (e: Throwable) {
			notificationManager.cancelAll()
			// TODO propagate error to view
			Log.e(this@BaseWorker::class.qualifiedName, "", e)
			return@coroutineScope Result.failure(workDataOf(KEY_FAILURE_CAUSE to e.message))
		}
		return@coroutineScope Result.success()
	}

	abstract suspend fun startService()

	private fun deserializeInputData() {
		val manifestString =
			inputData.getString(KEY_MANIFEST) ?: throw Exception("No manifest supplied")
		val configString = inputData.getString(KEY_CONFIG) ?: throw Exception("No config supplied")
		try {
			manifest = JsonSerializer.decodeFromString(manifestString)
		} catch (e: Throwable) {
			throw Exception("Invalid manifest", e)
		}
		try {
			config = JsonSerializer.decodeFromString(configString)
		} catch (e: Throwable) {
			throw Exception("Invalid config", e)
		}
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

	private fun CoroutineScope.collectProgress() = launch {
		service.progress.conflate().collect {
			val notification =
				progressNotificationBuilder.setProgress(it.max, it.progress, it.isIndeterminate)
					.build()
			notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
			delay(1000)
		}
	}

	private fun CoroutineScope.collectStatus() = launch {
		service.status.conflate().collect {
			val status = applicationContext.getString(it)
			val notification = progressNotificationBuilder.setContentText(status).build()
			notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
			delay(500)
		}
	}

	private fun CoroutineScope.collectMessages() = launch {
		service.message.collect {
			++MESSAGE_NOTIFICATION_ID
			val notification = createMessageNotification(it.titleId, it.messageId)
			notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification)
		}
	}

	companion object {
		const val KEY_MANIFEST = "WORKER_MANIFEST"
		const val KEY_CONFIG = "WORKER_CONFIG"
		const val KEY_FAILURE_CAUSE = "WORKER_FAILURE_CAUSE"
		const val PROGRESS_NOTIFICATION_ID = 813047

		@JvmStatic
		private var MESSAGE_NOTIFICATION_ID = 49725
	}
}