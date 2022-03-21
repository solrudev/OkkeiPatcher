package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import solru.okkeipatcher.domain.usecase.GetPatchSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.GetPatchWorkUseCase
import solru.okkeipatcher.domain.usecase.StartPatchWorkUseCase
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val startPatchWorkUseCase: StartPatchWorkUseCase,
	private val getPatchWorkUseCase: GetPatchWorkUseCase,
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() {
			val work = getPatchWorkUseCase()
			return work != null && work.currentState is WorkState.Running
		}

	private val canStartWork: Boolean
		get() {
			val work = getPatchWorkUseCase()
			return work == null || work.currentState is WorkState.Canceled || work.currentState is WorkState.Succeeded
		}

	init {
		if (canStartWork) {
			viewModelScope.launch {
				val patchSizeInMb = getPatchSizeInMbUseCase()
				val title = LocalizedString.resource(R.string.warning_start_patch_title)
				val message = LocalizedString.resource(R.string.warning_start_patch, patchSizeInMb)
				val startMessage = Message(title, message)
				updateUiState {
					val startWorkUiMessage = startWorkMessage.copy(data = startMessage)
					copy(startWorkMessage = startWorkUiMessage)
				}
			}
		}
	}

	override fun startWork() {
		val patchWork = startPatchWorkUseCase()
		workObservingScope.observeWork(patchWork)
	}

	override fun cancelWork() {
		getPatchWorkUseCase()?.cancel()
	}

	override fun onStart(owner: LifecycleOwner) {
		getPatchWorkUseCase()?.let {
			workObservingScope.observeWork(it)
		}
	}
}