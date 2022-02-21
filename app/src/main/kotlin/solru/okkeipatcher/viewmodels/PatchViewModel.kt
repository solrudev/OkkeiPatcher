package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val startPatchWorkUseCase: StartPatchWorkUseCase,
	private val getPatchWorkUuidUseCase: GetPatchWorkUuidUseCase,
	private val cancelWorkByIdUseCase: CancelWorkByIdUseCase,
	getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(getWorkStateFlowByIdUseCase, clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() = getPatchWorkUuidUseCase() != null

	override fun startWork() {
		val patchWorkId = startPatchWorkUseCase()
		workObservingScope.observeWork(patchWorkId)
	}

	override fun cancelWork() {
		getPatchWorkUuidUseCase()?.let {
			cancelWorkByIdUseCase(it)
		}
	}

	override fun onStart(owner: LifecycleOwner) {
		getPatchWorkUuidUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}