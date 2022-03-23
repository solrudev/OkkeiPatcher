package solru.okkeipatcher.viewmodels

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val getPatchWorkUseCase: GetPatchWorkUseCase,
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase,
	completeWorkUseCase: CompleteWorkUseCase,
	getIsWorkPendingUseCase: GetIsWorkPendingUseCase,
	clearNotificationsUseCase: ClearNotificationsUseCase
) : WorkViewModel(completeWorkUseCase, getIsWorkPendingUseCase, clearNotificationsUseCase) {

	init {
		viewModelScope.launch {
			if (!work.isPending()) {
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

	override val work: Work?
		get() = getPatchWorkUseCase()

	override suspend fun enqueueWork() = enqueuePatchWorkUseCase()
}