package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.domain.usecase.CancelWorkByIdUseCase
import solru.okkeipatcher.domain.usecase.GetRestoreWorkUuidUseCase
import solru.okkeipatcher.domain.usecase.GetWorkStateFlowByIdUseCase
import solru.okkeipatcher.domain.usecase.StartRestoreWorkUseCase
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
	private val startRestoreWorkUseCase: StartRestoreWorkUseCase,
	private val getRestoreWorkUuidUseCase: GetRestoreWorkUuidUseCase,
	private val cancelWorkByIdUseCase: CancelWorkByIdUseCase,
	getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase
) : WorkViewModel(getWorkStateFlowByIdUseCase) {

	override val isWorkRunning: Boolean
		get() = getRestoreWorkUuidUseCase() != null

	override fun startWork() {
		val restoreWorkId = startRestoreWorkUseCase()
		workObservingScope.observeWork(restoreWorkId)
	}

	override fun cancelWork() {
		getRestoreWorkUuidUseCase()?.let {
			cancelWorkByIdUseCase(it)
		}
	}

	override fun onStart(owner: LifecycleOwner) {
		getRestoreWorkUuidUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}