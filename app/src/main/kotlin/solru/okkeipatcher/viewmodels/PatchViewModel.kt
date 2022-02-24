package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val startPatchWorkUseCase: StartPatchWorkUseCase,
	private val getPatchWorkIdUseCase: GetPatchWorkIdUseCase,
	private val cancelWorkByIdUseCase: CancelWorkByIdUseCase,
	getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(getWorkStateFlowByIdUseCase, clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() = getPatchWorkIdUseCase() != null

	override fun startWork() {
		val patchWorkId = startPatchWorkUseCase()
		workObservingScope.observeWork(patchWorkId)
	}

	override fun cancelWork() {
		getPatchWorkIdUseCase()?.let {
			cancelWorkByIdUseCase(it)
		}
	}

	override fun onStart(owner: LifecycleOwner) {
		getPatchWorkIdUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}