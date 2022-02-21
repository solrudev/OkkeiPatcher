package solru.okkeipatcher.domain.usecase.impl

import android.app.NotificationManager
import androidx.core.content.getSystemService
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import javax.inject.Inject

class ClearNotificationsUseCaseImpl @Inject constructor() : ClearNotificationsUseCase {

	override fun invoke() {
		val notificationManager = OkkeiApplication.context.getSystemService<NotificationManager>()
		notificationManager?.cancelAll()
	}
}