package ru.solrudev.okkeipatcher.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

private val globalProgressNotificationId = AtomicInteger(813047)
private val globalMessageNotificationId = AtomicInteger(49725)

interface NotificationService {
	var progressNotificationTitle: LocalizedString
	val progressNotificationId: Int
	fun getProgressNotification(): Notification
	fun updateProgressNotification(status: LocalizedString, progressData: ProgressData)
	suspend fun displayMessageNotification(message: Message)
	suspend fun displayResultNotification(message: Message)
	suspend fun clearShownMessageNotifications()
}

class NotificationServiceImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : NotificationService {

	override var progressNotificationTitle: LocalizedString = LocalizedString.empty()
		set(value) {
			if (value != field) {
				field = value
				val titleString = value.resolve(applicationContext)
				progressNotificationBuilder.setContentTitle(titleString)
			}
		}

	override val progressNotificationId = globalProgressNotificationId.incrementAndGet()

	private val progressNotificationBuilder =
		createNotificationBuilder(progressNotificationTitle, progressNotification = true)

	private val notificationManager = applicationContext.getSystemService<NotificationManager>()
	private val shownMessageNotifications = mutableListOf<Int>()
	private val shownMessageNotificationsMutex = Mutex()

	override fun getProgressNotification() = progressNotificationBuilder.build()

	override fun updateProgressNotification(status: LocalizedString, progressData: ProgressData) {
		val statusString = status.resolve(applicationContext)
		val notification = progressNotificationBuilder
			.setContentText(statusString)
			.setProgress(progressData.max, progressData.progress, false)
			.build()
		notificationManager?.notify(progressNotificationId, notification)
	}

	override suspend fun displayMessageNotification(message: Message) =
		displayMessageNotification(message, resultMessage = false)

	override suspend fun displayResultNotification(message: Message) =
		displayMessageNotification(message, resultMessage = true)

	override suspend fun clearShownMessageNotifications() {
		shownMessageNotificationsMutex.withLock {
			shownMessageNotifications.forEach {
				notificationManager?.cancel(it)
			}
			shownMessageNotifications.clear()
		}
	}

	private suspend inline fun displayMessageNotification(message: Message, resultMessage: Boolean) {
		val messageString = message.text.resolve(applicationContext)
		val notification = createNotificationBuilder(message.title, progressNotification = false).apply {
			setContentText(messageString)
			if (messageString.length > 28) {
				setStyle(NotificationCompat.BigTextStyle().bigText(messageString))
			}
		}.build()
		val notificationId = globalMessageNotificationId.incrementAndGet()
		if (!resultMessage) {
			shownMessageNotificationsMutex.withLock {
				shownMessageNotifications.add(notificationId)
			}
		}
		notificationManager?.notify(notificationId, notification)
	}

	private fun createNotificationBuilder(
		title: LocalizedString,
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
		val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
		val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
		return PendingIntent.getActivity(
			applicationContext,
			0,
			launchIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or flagImmutable
		)
	}
}