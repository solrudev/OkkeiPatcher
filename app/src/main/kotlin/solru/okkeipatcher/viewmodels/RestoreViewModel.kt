package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
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

	init {
		if (!isWorkRunning) {
			val title = LocalizedString.resource(R.string.warning)
			val message = LocalizedString.resource(R.string.warning_start_process)
			_startWorkMessage.value = Message(title, message)
		}
	}

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