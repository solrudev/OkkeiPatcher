package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsPatchedUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.*
import ru.solrudev.okkeipatcher.ui.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getIsPatchedUseCase: GetIsPatchedUseCase,
	private val getIsWorkPendingUseCase: GetIsWorkPendingUseCase,
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCase,
	private val getPatchWorkUseCase: GetPatchWorkUseCase,
	private val getRestoreWorkUseCase: GetRestoreWorkUseCase,
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase,
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase
) : ViewModel(), Flow<HomeUiState>, DefaultLifecycleObserver {

	private val uiState = MutableStateFlow(HomeUiState())

	init {
		viewModelScope.launch {
			awaitAll(
				async { checkIsPatched() },
				async { checkPendingWork() }
			)
			checkPatchUpdates()
		}
	}

	override suspend fun collect(collector: FlowCollector<HomeUiState>) = uiState.collect(collector)
	override fun onStop(owner: LifecycleOwner) = hideAllMessages()

	fun promptPatch() {
		viewModelScope.launch {
			uiState.update {
				it.copy(isPatchSizeLoading = true)
			}
			val patchSizeInMb = getPatchSizeInMbUseCase()
			val title = LocalizedString.resource(R.string.warning_start_patch_title)
			val message = LocalizedString.resource(R.string.warning_start_patch, patchSizeInMb)
			val startMessage = Message(title, message)
			uiState.update {
				val startPatchMessage = it.startPatchMessage.copy(data = startMessage)
				it.copy(
					isPatchSizeLoading = false,
					startPatchMessage = startPatchMessage
				)
			}
		}
	}

	fun promptRestore() {
		val title = LocalizedString.resource(R.string.warning_start_restore_title)
		val message = LocalizedString.resource(R.string.warning_abort)
		val startMessage = Message(title, message)
		uiState.update {
			val startRestoreMessage = it.startRestoreMessage.copy(data = startMessage)
			it.copy(startRestoreMessage = startRestoreMessage)
		}
	}

	fun startPatch() {
		viewModelScope.launch {
			val patchWork = enqueuePatchWorkUseCase()
			setPendingWork(patchWork)
		}
	}

	fun startRestore() {
		viewModelScope.launch {
			val restoreWork = enqueueRestoreWorkUseCase()
			setPendingWork(restoreWork)
		}
	}

	fun workSucceeded() {
		viewModelScope.launch {
			checkIsPatched()
		}
	}

	fun patchUpdatesMessageShown() = uiState.update {
		it.copy(canShowPatchUpdatesMessage = false)
	}

	fun navigatedToWorkScreen() = uiState.update {
		it.copy(
			pendingWork = null,
			canShowPatchUpdatesMessage = false
		)
	}

	fun showStartPatchMessage() = uiState.update {
		val startPatchMessage = it.startPatchMessage.copy(isVisible = true)
		it.copy(startPatchMessage = startPatchMessage)
	}

	fun showStartRestoreMessage() = uiState.update {
		val startRestoreMessage = it.startRestoreMessage.copy(isVisible = true)
		it.copy(startRestoreMessage = startRestoreMessage)
	}

	fun dismissStartPatchMessage() = uiState.update {
		it.copy(startPatchMessage = MessageUiState())
	}

	fun dismissStartRestoreMessage() = uiState.update {
		it.copy(startRestoreMessage = MessageUiState())
	}

	private suspend fun checkPendingWork() {
		val patchWork = getPatchWorkUseCase()
		val restoreWork = getRestoreWorkUseCase()
		val pendingWork = pendingWorkOrNull(patchWork) ?: pendingWorkOrNull(restoreWork)
		if (pendingWork != null) {
			setPendingWork(pendingWork)
		}
	}

	/**
	 * Returns [work] if it's pending, `null` otherwise.
	 */
	private suspend fun pendingWorkOrNull(work: Work?) =
		if (work != null && getIsWorkPendingUseCase(work)) {
			work
		} else {
			null
		}

	private fun setPendingWork(work: Work) = uiState.update {
		it.copy(pendingWork = work)
	}

	private suspend fun checkIsPatched() {
		val isPatched = getIsPatchedUseCase()
		uiState.update {
			it.copy(
				isPatchEnabled = !isPatched,
				isRestoreEnabled = isPatched
			)
		}
	}

	private suspend fun checkPatchUpdates() {
		val updatesAvailable = getPatchUpdatesUseCase().available
		uiState.update {
			it.copy(
				isPatchEnabled = updatesAvailable || it.isPatchEnabled,
				patchUpdatesAvailable = updatesAvailable
			)
		}
	}

	private fun hideAllMessages() = uiState.update {
		val startPatchMessage = it.startPatchMessage.copy(isVisible = false)
		val startRestoreMessage = it.startRestoreMessage.copy(isVisible = false)
		it.copy(
			startPatchMessage = startPatchMessage,
			startRestoreMessage = startRestoreMessage
		)
	}
}