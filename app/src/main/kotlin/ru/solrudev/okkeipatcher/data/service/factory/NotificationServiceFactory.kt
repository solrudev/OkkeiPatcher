package ru.solrudev.okkeipatcher.data.service.factory

import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.service.NotificationService
import ru.solrudev.okkeipatcher.data.service.NotificationServiceImpl
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

interface NotificationServiceFactory {
	fun create(progressNotificationTitle: LocalizedString, contentIntent: PendingIntent): NotificationService
}

class NotificationServiceFactoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : NotificationServiceFactory {

	override fun create(progressNotificationTitle: LocalizedString, contentIntent: PendingIntent): NotificationService {
		return NotificationServiceImpl(applicationContext, progressNotificationTitle, contentIntent)
	}
}