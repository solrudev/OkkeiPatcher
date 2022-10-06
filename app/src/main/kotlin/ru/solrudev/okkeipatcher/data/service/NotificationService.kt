package ru.solrudev.okkeipatcher.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import java.util.concurrent.atomic.AtomicInteger

private val globalProgressNotificationId = AtomicInteger(813047)
private val globalMessageNotificationId = AtomicInteger(49725)

interface NotificationService {
	val progressNotificationId: Int
	fun getProgressNotification(): Notification
	fun updateProgressNotification(status: LocalizedString, progressData: ProgressData)
	suspend fun displayMessageNotification(message: Message)
	suspend fun displayResultNotification(message: Message)
	suspend fun clearShownMessageNotifications()
}

class NotificationServiceImpl(
	private val applicationContext: Context,
	private val progressNotificationTitle: LocalizedString,
	private val contentIntent: PendingIntent
) : NotificationService {

	override val progressNotificationId = globalProgressNotificationId.incrementAndGet()

	private val progressNotificationBuilder =
		createNotificationBuilder(progressNotificationTitle, progressNotification = true)

	private val notificationManager = applicationContext.getSystemService<NotificationManager>()
	private val shownMessageNotifications = mutableListOf<Int>()
	private val shownMessageNotificationsMutex = Mutex()

	override fun getProgressNotification() = progressNotificationBuilder.build()

	override fun updateProgressNotification(status: LocalizedString, progressData: ProgressData) {
		val titleString = progressNotificationTitle.resolve(applicationContext)
		val statusString = status.resolve(applicationContext)
		val notification = progressNotificationBuilder
			.setContentTitle(titleString)
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
}