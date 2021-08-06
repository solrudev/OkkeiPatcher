package solru.okkeipatcher.pm.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R

private val notificationManager: NotificationManager by lazy {
	MainApplication.context.getSystemService(
		Context.NOTIFICATION_SERVICE
	) as NotificationManager
}

internal fun showNotification(
	intent: PendingIntent,
	notificationId: Int,
	titleId: Int,
	messageId: Int
) {
	val channelId = MainApplication.context.getString(R.string.notification_channel_pm_id)
	val title = MainApplication.context.getString(titleId)
	val message = MainApplication.context.getString(messageId)
	val notification =
		NotificationCompat.Builder(MainApplication.context, channelId).apply {
			setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			setContentTitle(title)
			setContentText(message)
			setContentIntent(intent)
			priority = NotificationCompat.PRIORITY_MAX
			setDefaults(NotificationCompat.DEFAULT_ALL)
			setSmallIcon(R.mipmap.ic_launcher_foreground)
			setOngoing(true)
			setFullScreenIntent(intent, true)
			setAutoCancel(true)
		}.build()
	notificationManager.notify(notificationId, notification)
}