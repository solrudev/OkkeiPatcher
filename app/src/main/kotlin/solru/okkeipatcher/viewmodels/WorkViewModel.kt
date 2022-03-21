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
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import solru.okkeipatcher.ui.state.UiMessage
import solru.okkeipatcher.ui.state.WorkUiState

abstract class WorkViewModel(
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
		val title = LocalizedString.resource(R.string.warning_abort_title)
		val message = LocalizedString.resource(R.string.warning_abort)
		val cancelMessage = Message(title, message)
		updateUiState {
			val cancelWorkUiMessage = cancelWorkMessage.copy(data = cancelMessage)
			copy(cancelWorkMessage = cancelWorkUiMessage)
		}
	}

	fun showStartWorkMessage() = updateUiState {
		val startWorkUiMessage = startWorkMessage.copy(isVisible = true)
		copy(startWorkMessage = startWorkUiMessage)
	}

	fun showCancelWorkMessage() = updateUiState {
		val cancelWorkUiMessage = cancelWorkMessage.copy(isVisible = true)
		copy(cancelWorkMessage = cancelWorkUiMessage)
	}

	fun showErrorMessage() = updateUiState {
		val errorUiMessage = errorMessage.copy(isVisible = true)
		copy(errorMessage = errorUiMessage)
	}

	fun closeStartWorkMessage() = updateUiState {
		copy(startWorkMessage = UiMessage())
	}

	fun closeCancelWorkMessage() = updateUiState {
		copy(cancelWorkMessage = UiMessage())
	}

	fun closeErrorMessage() = updateUiState {
		copy(errorMessage = UiMessage())
	}

	fun hideAllMessages() = updateUiState {
		val startWorkUiMessage = startWorkMessage.copy(isVisible = false)
		val cancelWorkUiMessage = cancelWorkMessage.copy(isVisible = false)
		val errorUiMessage = errorMessage.copy(isVisible = false)
		copy(
			startWorkMessage = startWorkUiMessage,
			cancelWorkMessage = cancelWorkUiMessage,
			errorMessage = errorUiMessage
		)
	}

	protected fun updateUiState(reduce: WorkUiState.() -> WorkUiState) {
		_uiState.value = _uiState.value.reduce()
	}

	protected fun CoroutineScope.observeWork(work: Work) = launch {
		if (isWorkObserved) {
			return@launch
		}
		isWorkObserved = true
		work.state
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
		val message = Message(
			LocalizedString.resource(R.string.exception),
			LocalizedString.raw(stackTrace)
		)
		updateUiState {
			val errorUiMessage = errorMessage.copy(data = message)
			copy(errorMessage = errorUiMessage)
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