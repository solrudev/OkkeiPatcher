package solru.okkeipatcher.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val startPatchWorkUseCase: StartPatchWorkUseCase,
	private val getPatchWorkIdUseCase: GetPatchWorkIdUseCase,
	private val cancelWorkByIdUseCase: CancelWorkByIdUseCase,
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase,
	getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(getWorkStateFlowByIdUseCase, clearNotificationsUseCase) {

	override val isWorkRunning: Boolean
		get() = getPatchWorkIdUseCase() != null

	init {
		if (!isWorkRunning) {
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