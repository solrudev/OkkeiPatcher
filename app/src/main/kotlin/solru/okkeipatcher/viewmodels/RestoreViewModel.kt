package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
	private val startRestoreWorkUseCase: StartRestoreWorkUseCase,
	private val getRestoreWorkIdUseCase: GetRestoreWorkIdUseCase,
	private val cancelWorkByIdUseCase: CancelWorkByIdUseCase,
	getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(getWorkStateFlowByIdUseCase, clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() = getRestoreWorkIdUseCase() != null

	override fun startWork() {
		val restoreWorkId = startRestoreWorkUseCase()
		workObservingScope.observeWork(restoreWorkId)
	}

	override fun cancelWork() {
		getRestoreWorkIdUseCase()?.let {
			cancelWorkByIdUseCase(it)
		}
	}

	override fun onStart(owner: LifecycleOwner) {
		getRestoreWorkIdUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}