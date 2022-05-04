package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.usecase.patch.factory.GetPatchSizeInMbUseCaseFactory
import ru.solrudev.okkeipatcher.domain.usecase.work.*
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val getPatchWorkUseCase: GetPatchWorkUseCase,
	private val getPatchSizeInMbUseCaseFactory: GetPatchSizeInMbUseCaseFactory,
	getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	cancelWorkUseCase: CancelWorkUseCase,
	completeWorkUseCase: CompleteWorkUseCase,
	getIsWorkPendingUseCase: GetIsWorkPendingUseCase
) : WorkViewModel(
	getWorkStateFlowUseCase,
	cancelWorkUseCase,
	completeWorkUseCase,
	getIsWorkPendingUseCase
) {

	init {
		viewModelScope.launch {
			if (!work.isPending()) {
				uiState.update {
					it.copy(isLoading = true)
				}
				val getPatchSizeInMbUseCase = getPatchSizeInMbUseCaseFactory.create()
				val patchSizeInMb = getPatchSizeInMbUseCase()
				val title = LocalizedString.resource(R.string.warning_start_patch_title)
				val message = LocalizedString.resource(R.string.warning_start_patch, patchSizeInMb)
				val startMessage = Message(title, message)
				uiState.update {
					val startWorkUiMessage = it.startWorkMessage.copy(data = startMessage)
					it.copy(
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