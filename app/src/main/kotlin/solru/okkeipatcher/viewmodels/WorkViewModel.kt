package solru.okkeipatcher.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import solru.okkeipatcher.domain.usecase.GetWorkStateFlowByIdUseCase
import solru.okkeipatcher.ui.state.WorkUiState
import java.util.*

abstract class WorkViewModel(
	private val getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	private val clearNotificationsUseCase: ClearNotificationsUseCase
) : ViewModel(), DefaultLifecycleObserver {

	protected val workObservingScope = CoroutineScope(Dispatchers.Main.immediate)
	private var isWorkObserved = false

	private val _uiState = MutableStateFlow(WorkUiState())
	val uiState = _uiState.asStateFlow()

	abstract val isWorkRunning: Boolean

	abstract fun startWork()
	abstract fun cancelWork()

	override fun onCleared() {
		workObservingScope.cancel()
	}

	override fun onStop(owner: LifecycleOwner) {
		workObservingScope.coroutineContext[Job]?.cancelChildren()
	}

	fun cancel() {
		val title = LocalizedString.resource(R.string.warning)
		val message = LocalizedString.resource(R.string.warning_abort)
		val cancelWorkMessage = Message(title, message)
		updateUiState {
			copy(cancelWorkMessage = cancelWorkMessage)
		}
	}

	fun showStartWorkMessage() = updateUiState {
		copy(isStartWorkMessageVisible = true)
	}

	fun showCancelWorkMessage() = updateUiState {
		copy(isCancelWorkMessageVisible = true)
	}

	fun showErrorMessage() = updateUiState {
		copy(isErrorMessageVisible = true)
	}

	fun hideStartWorkMessage() = updateUiState {
		copy(isStartWorkMessageVisible = false)
	}

	fun hideCancelWorkMessage() = updateUiState {
		copy(isCancelWorkMessageVisible = false)
	}

	fun hideErrorMessage() = updateUiState {
		copy(isErrorMessageVisible = false)
	}

	fun closeStartWorkMessage() = updateUiState {
		copy(
			startWorkMessage = null,
			isStartWorkMessageVisible = false
		)
	}

	fun closeCancelWorkMessage() = updateUiState {
		copy(
			cancelWorkMessage = null,
			isCancelWorkMessageVisible = false
		)
	}

	fun closeErrorMessage() = updateUiState {
		copy(
			errorMessage = null,
			isErrorMessageVisible = false
		)
	}

	protected fun updateUiState(reduce: WorkUiState.() -> WorkUiState) {
		_uiState.value = _uiState.value.reduce()
	}

	protected fun CoroutineScope.observeWork(workId: UUID) = launch {
		if (isWorkObserved) {
			return@launch
		}
		isWorkObserved = true
		getWorkStateFlowByIdUseCase(workId)
			.onCompletion { isWorkObserved = false }
			.collect { workState ->
				when (workState) {
					is WorkState.Running -> onWorkRunning(workState)
					is WorkState.Failed -> onWorkFailed(workState)
					is WorkState.Succeeded -> onWorkSucceeded()
					is WorkState.Canceled -> onWorkCanceled()
					is WorkState.Unknown -> {}
				}
			}
	}

	private fun onWorkRunning(workState: WorkState.Running) = updateUiState {
		copy(
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
		updateUiState {
			copy(errorMessage = errorMessage)
		}
		clearNotificationsUseCase()
	}

	private fun onWorkSucceeded() {
		updateUiState {
			val maxProgress = progressData.copy(progress = progressData.max)
			copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
		clearNotificationsUseCase()
	}

	private fun onWorkCanceled() = updateUiState {
		copy(isWorkCanceled = true)
	}
}