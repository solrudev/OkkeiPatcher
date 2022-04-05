package solru.okkeipatcher.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.usecase.work.*
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
	private val enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCase,
	private val getRestoreWorkUseCase: GetRestoreWorkUseCase,
	getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	cancelWorkUseCase: CancelWorkUseCase,
	completeWorkUseCase: CompleteWorkUseCase,
	getIsWorkPendingUseCase: GetIsWorkPendingUseCase
) : WorkViewModel(getWorkStateFlowUseCase, cancelWorkUseCase, completeWorkUseCase, getIsWorkPendingUseCase) {

	init {
		viewModelScope.launch {
			if (!work.isPending()) {
				val title = LocalizedString.resource(R.string.warning_start_restore_title)
				val message = LocalizedString.resource(R.string.warning_abort)
				val startMessage = Message(title, message)
				updateUiState {
					val startWorkUiMessage = startWorkMessage.copy(data = startMessage)
					copy(startWorkMessage = startWorkUiMessage)
				}
			}
		}
	}

	override val work: Work?
		get() = getRestoreWorkUseCase()

	override suspend fun enqueueWork() = enqueueRestoreWorkUseCase()
}