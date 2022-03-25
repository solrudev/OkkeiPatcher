package solru.okkeipatcher.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.usecase.common.ClearNotificationsUseCase
import solru.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import solru.okkeipatcher.domain.usecase.work.EnqueuePatchWorkUseCase
import solru.okkeipatcher.domain.usecase.work.GetIsWorkPendingUseCase
import solru.okkeipatcher.domain.usecase.work.GetPatchWorkUseCase
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
				updateUiState {
					copy(isLoading = true)
				}
				val patchSizeInMb = getPatchSizeInMbUseCase()
				val title = LocalizedString.resource(R.string.warning_start_patch_title)
				val message = LocalizedString.resource(R.string.warning_start_patch, patchSizeInMb)
				val startMessage = Message(title, message)
				updateUiState {
					val startWorkUiMessage = startWorkMessage.copy(data = startMessage)
					copy(
						isLoading = false,
						startWorkMessage = startWorkUiMessage
					)
				}
			}
		}
	}

	override val work: Work?
		get() = getPatchWorkUseCase()

	override suspend fun enqueueWork() = enqueuePatchWorkUseCase()
}