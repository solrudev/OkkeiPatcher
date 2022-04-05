package solru.okkeipatcher.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.model.WorkState
import solru.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import solru.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import solru.okkeipatcher.domain.usecase.work.GetIsWorkPendingUseCase
import solru.okkeipatcher.domain.usecase.work.GetWorkStateFlowUseCase
import solru.okkeipatcher.ui.model.MessageUiState
import solru.okkeipatcher.ui.model.WorkUiState

abstract class WorkViewModel(
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase,
	private val completeWorkUseCase: CompleteWorkUseCase,
	private val getIsWorkPendingUseCase: GetIsWorkPendingUseCase
) : ViewModel(), DefaultLifecycleObserver {

	protected val _uiState = MutableStateFlow(WorkUiState())
	val uiState = _uiState.asStateFlow()

	protected abstract val work: Work?
	private var isWorkObserved = false
	private val workObservingScope = CoroutineScope(Dispatchers.Main.immediate)

	protected abstract suspend fun enqueueWork(): Work

	fun startWork() {
		viewModelScope.launch {
			val enqueuedWork = enqueueWork()
			enqueuedWork.observe()
			setIsButtonEnabled(true)
		}
	}

	fun cancelWork() {
		work?.let {
			cancelWorkUseCase(it)
		}
	}

	override fun onCleared() {
		workObservingScope.cancel()
	}

	override fun onStart(owner: LifecycleOwner) {
		viewModelScope.launch {
			if (work.isPending()) {
				work.observe()
				setIsButtonEnabled(true)
			}
		}
	}

	override fun onStop(owner: LifecycleOwner) {
		hideAllMessages()
		workObservingScope.coroutineContext[Job]?.cancelChildren()
	}

	fun requestWorkCancel() {
		val title = LocalizedString.resource(R.string.warning_abort_title)
		val message = LocalizedString.resource(R.string.warning_abort)
		val cancelMessage = Message(title, message)
		_uiState.update {
			val cancelWorkMessage = it.cancelWorkMessage.copy(data = cancelMessage)
			it.copy(cancelWorkMessage = cancelWorkMessage)
		}
	}

	fun showStartWorkMessage() = _uiState.update {
		val startWorkMessage = it.startWorkMessage.copy(isVisible = true)
		it.copy(startWorkMessage = startWorkMessage)
	}

	fun showCancelWorkMessage() = _uiState.update {
		val cancelWorkMessage = it.cancelWorkMessage.copy(isVisible = true)
		it.copy(cancelWorkMessage = cancelWorkMessage)
	}

	fun showErrorMessage() = _uiState.update {
		val errorMessage = it.errorMessage.copy(isVisible = true)
		it.copy(errorMessage = errorMessage)
	}

	fun closeStartWorkMessage() = _uiState.update {
		it.copy(startWorkMessage = MessageUiState())
	}

	fun closeCancelWorkMessage() = _uiState.update {
		it.copy(cancelWorkMessage = MessageUiState())
	}

	fun closeErrorMessage() = _uiState.update {
		it.copy(errorMessage = MessageUiState())
	}

	protected suspend fun Work?.isPending() = this?.let {
		getIsWorkPendingUseCase(it)
	} ?: false

	private fun setIsButtonEnabled(value: Boolean) {
		_uiState.update {
			it.copy(isButtonEnabled = value)
		}
	}

	private fun hideAllMessages() = _uiState.update {
		val startWorkMessage = it.startWorkMessage.copy(isVisible = false)
		val cancelWorkMessage = it.cancelWorkMessage.copy(isVisible = false)
		val errorMessage = it.errorMessage.copy(isVisible = false)
		it.copy(
			startWorkMessage = startWorkMessage,
			cancelWorkMessage = cancelWorkMessage,
			errorMessage = errorMessage
		)
	}

	private fun Work?.observe() = this?.let { work ->
		workObservingScope.launch {
			getWorkStateFlowUseCase(work)
				.takeUnless { isWorkObserved }
				?.onStart { isWorkObserved = true }
				?.onCompletion { isWorkObserved = false }
				?.collect { workState ->
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

	private fun onWorkRunning(workState: WorkState.Running) = _uiState.update {
		it.copy(
			status = workState.status,
			progressData = workState.progressData
		)
	}

	private fun onWorkFailed(workState: WorkState.Failed) {
		setIsButtonEnabled(false)
		val stackTrace = workState.throwable?.stackTraceToString() ?: "null"
		val message = Message(
			LocalizedString.resource(R.string.exception),
			LocalizedString.raw(stackTrace)
		)
		_uiState.update {
			val errorMessage = it.errorMessage.copy(data = message)
			it.copy(errorMessage = errorMessage)
		}
	}

	private fun onWorkSucceeded() {
		_uiState.update {
			val maxProgress = it.progressData.copy(progress = it.progressData.max)
			it.copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
	}

	private fun onWorkCanceled() = _uiState.update {
		it.copy(isWorkCanceled = true)
	}
}