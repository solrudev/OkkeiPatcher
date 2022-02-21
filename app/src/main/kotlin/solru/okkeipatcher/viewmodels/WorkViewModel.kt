package solru.okkeipatcher.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.ClearNotificationsUseCase
import solru.okkeipatcher.domain.usecase.GetWorkStateFlowByIdUseCase
import java.util.*

abstract class WorkViewModel(
	private val getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCase,
	private val clearNotificationsUseCase: ClearNotificationsUseCase
) : ViewModel(), DefaultLifecycleObserver {

	protected val workObservingScope = CoroutineScope(Dispatchers.Main.immediate)
	private var isWorkObserved = false

	private val _status = MutableStateFlow<LocalizedString>(LocalizedString.empty())
	private val _progressData = MutableStateFlow(ProgressData())
	private val _errorMessage = MutableStateFlow(Message.empty)
	private val _buttonText = MutableStateFlow<LocalizedString>(LocalizedString.resource(R.string.abort))
	private val _workSucceeded = MutableSharedFlow<Unit>()
	private val _workCanceled = MutableSharedFlow<Unit>()
	val status = _status.asStateFlow()
	val progressData = _progressData.asStateFlow()
	val errorMessage = _errorMessage.asStateFlow()
	val buttonText = _buttonText.asStateFlow()
	val workSucceeded = _workSucceeded.asSharedFlow()
	val workCanceled = _workCanceled.asSharedFlow()

	abstract val isWorkRunning: Boolean

	abstract fun startWork()
	abstract fun cancelWork()

	override fun onCleared() {
		workObservingScope.cancel()
	}

	override fun onStop(owner: LifecycleOwner) {
		workObservingScope.coroutineContext[Job]?.cancelChildren()
	}

	fun closeErrorMessage() {
		_errorMessage.value = Message.empty
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
					is WorkState.Running -> {
						_status.value = workState.status
						_progressData.value = workState.progressData
					}
					is WorkState.Failed -> {
						val stackTrace = workState.throwable?.stackTraceToString() ?: "null"
						_errorMessage.value = Message(
							LocalizedString.resource(R.string.exception),
							LocalizedString.raw(stackTrace)
						)
						clearNotificationsUseCase()
					}
					WorkState.Succeeded -> {
						_buttonText.value = LocalizedString.resource(R.string.dialog_button_ok)
						_status.value = LocalizedString.resource(R.string.status_succeeded)
						_workSucceeded.emit(Unit)
						clearNotificationsUseCase()
					}
					WorkState.Canceled -> _workCanceled.emit(Unit)
					WorkState.Unknown -> {}
				}
			}
	}
}