package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsPatchedUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.factory.GetPatchUpdatesUseCaseFactory
import ru.solrudev.okkeipatcher.ui.model.HomeUiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getGetIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCaseFactory: GetPatchUpdatesUseCaseFactory,
	private val getIsPatchedUseCase: GetIsPatchedUseCase
) : ViewModel(), Flow<HomeUiState>, DefaultLifecycleObserver {

	private val uiState = MutableStateFlow(HomeUiState())

	override suspend fun collect(collector: FlowCollector<HomeUiState>) = uiState.collect(collector)

	override fun onCreate(owner: LifecycleOwner) {
		viewModelScope.launch {
			val isPatched = getIsPatchedUseCase()
			uiState.update {
				it.copy(
					isPatchEnabled = !isPatched || it.patchUpdatesAvailable,
					isRestoreEnabled = isPatched
				)
			}
		}
	}

	fun checkPatchUpdates() {
		viewModelScope.launch {
			val getPatchUpdatesUseCase = getPatchUpdatesUseCaseFactory.create()
			val updatesAvailable = getPatchUpdatesUseCase().available
			val isPatched = getIsPatchedUseCase()
			uiState.update {
				it.copy(
					isPatchEnabled = updatesAvailable || !isPatched,
					patchUpdatesAvailable = updatesAvailable,
					checkedForPatchUpdates = true
				)
			}
		}
	}

	fun patchUpdatesMessageShown() = uiState.update {
		it.copy(patchUpdatesMessageShown = true)
	}
}