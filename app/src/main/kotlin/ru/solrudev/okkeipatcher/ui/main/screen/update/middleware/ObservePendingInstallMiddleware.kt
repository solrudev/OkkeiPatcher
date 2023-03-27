package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.yield
import ru.solrudev.okkeipatcher.app.usecase.GetIsUpdateInstallPendingFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.AwaitingInstallation
import javax.inject.Inject

class ObservePendingInstallMiddleware @Inject constructor(
	private val getIsUpdateInstallPendingFlowUseCase: GetIsUpdateInstallPendingFlowUseCase
) : Middleware<UpdateEvent> {

	override fun apply(events: Flow<UpdateEvent>) = getIsUpdateInstallPendingFlowUseCase()
		.filter { it }
		.map {
			yield()
			UpdateStatusChanged(AwaitingInstallation)
		}
}