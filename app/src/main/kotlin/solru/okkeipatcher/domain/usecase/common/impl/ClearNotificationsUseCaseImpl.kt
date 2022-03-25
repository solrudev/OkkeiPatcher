package solru.okkeipatcher.domain.usecase.common.impl

import android.app.NotificationManager
import androidx.core.content.getSystemService
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.common.ClearNotificationsUseCase
import javax.inject.Inject

class ClearNotificationsUseCaseImpl @Inject constructor() : ClearNotificationsUseCase {

	override fun invoke() {
		val notificationManager = OkkeiApplication.context.getSystemService<NotificationManager>()
		notificationManager?.cancelAll()
	}
}