package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import solru.okkeipatcher.domain.usecase.GetRestoreWorkUseCase
import solru.okkeipatcher.domain.usecase.StartRestoreWorkUseCase
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
	private val startRestoreWorkUseCase: StartRestoreWorkUseCase,
	private val getRestoreWorkUseCase: GetRestoreWorkUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() {
			val work = getRestoreWorkUseCase()
			return work != null && work.currentState is WorkState.Running
		}

	private val canStartWork: Boolean
		get() {
			val work = getRestoreWorkUseCase()
			return work == null || work.currentState is WorkState.Canceled || work.currentState is WorkState.Succeeded
		}

	init {
		if (canStartWork) {
			val title = LocalizedString.resource(R.string.warning_start_restore_title)
			val message = LocalizedString.resource(R.string.warning_abort)
			val startMessage = Message(title, message)
			updateUiState {
				val startWorkUiMessage = startWorkMessage.copy(data = startMessage)
				copy(startWorkMessage = startWorkUiMessage)
			}
		}
	}

	override fun startWork() {
		val restoreWork = startRestoreWorkUseCase()
		workObservingScope.observeWork(restoreWork)
	}

	override fun cancelWork() {
		getRestoreWorkUseCase()?.cancel()
	}

	override fun onStart(owner: LifecycleOwner) {
		getRestoreWorkUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}