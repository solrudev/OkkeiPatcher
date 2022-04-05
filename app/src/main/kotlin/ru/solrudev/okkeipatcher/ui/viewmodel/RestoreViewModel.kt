package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.usecase.work.*
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
				_uiState.update {
					val startWorkMessage = it.startWorkMessage.copy(data = startMessage)
					it.copy(startWorkMessage = startWorkMessage)
				}
			}
		}
	}

	override val work: Work?
		get() = getRestoreWorkUseCase()

	override suspend fun enqueueWork() = enqueueRestoreWorkUseCase()
}