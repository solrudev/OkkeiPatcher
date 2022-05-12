package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.WorkState
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.model.WorkUiState
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase,
	private val completeWorkUseCase: CompleteWorkUseCase
) : ViewModel(), Flow<WorkUiState> {

	private val uiState = MutableStateFlow(WorkUiState())

	override suspend fun collect(collector: FlowCollector<WorkUiState>) = uiState.collect(collector)

	fun observeWork(work: Work) {
		viewModelScope.launch {
			getWorkStateFlowUseCase(work).collect { workState ->
				if (workState.isFinished) {
					completeWorkUseCase(work)
				}
				when (workState) {
					is WorkState.Running -> onWorkRunning(workState)
					is WorkState.Failed -> onWorkFailed(workState)
					is WorkState.Succeeded -> onWorkSucceeded()
					is WorkState.Canceled -> onWorkCanceled()
					is WorkState.Unknown -> {}
				}
			}
		}
	}

	fun stopObservingWork() = viewModelScope.coroutineContext[Job]?.cancelChildren()
	fun cancelWork(work: Work) = cancelWorkUseCase(work)

	fun promptCancelWork() {
		val title = LocalizedString.resource(R.string.warning_abort_title)
		val message = LocalizedString.resource(R.string.warning_abort)
		val cancelWorkMessage = Message(title, message)
		uiState.update {
			it.copy(cancelWorkMessage = cancelWorkMessage)
		}
	}

	fun dismissCancelWorkMessage() = uiState.update {
		it.copy(cancelWorkMessage = Message.empty)
	}

	fun dismissErrorMessage() = uiState.update {
		it.copy(error = Message.empty)
	}

	fun onAnimationsPlayed() = uiState.update {
		it.copy(animationsPlayed = true)
	}

	private fun onWorkRunning(workState: WorkState.Running) = uiState.update {
		it.copy(
			status = workState.status,
			progressData = workState.progressData
		)
	}

	private fun onWorkFailed(workState: WorkState.Failed) {
		val stackTrace = workState.throwable?.stackTraceToString() ?: "null"
		val errorMessage = Message(
			LocalizedString.resource(R.string.exception),
			LocalizedString.raw(stackTrace)
		)
		uiState.update {
			it.copy(error = errorMessage)
		}
	}

	private fun onWorkSucceeded() {
		uiState.update {
			val maxProgress = it.progressData.copy(progress = it.progressData.max)
			it.copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
	}

	private fun onWorkCanceled() = uiState.update {
		it.copy(isWorkCanceled = true)
	}
}