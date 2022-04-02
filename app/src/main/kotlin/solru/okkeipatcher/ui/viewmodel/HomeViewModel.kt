package solru.okkeipatcher.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import solru.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import solru.okkeipatcher.ui.model.HomeUiState
import solru.okkeipatcher.util.Preferences
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getGetIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCaseProvider: Provider<GetPatchUpdatesUseCase>
) : ViewModel(), DefaultLifecycleObserver {

	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()

	override fun onCreate(owner: LifecycleOwner) = updateUiState {
		copy(
			isPatchEnabled = !isPatched() || patchUpdatesAvailable,
			isRestoreEnabled = isPatched()
		)
	}

	fun checkPatchUpdates() {
		viewModelScope.launch {
			val getPatchUpdatesUseCase = getPatchUpdatesUseCaseProvider.get()
			val updatesAvailable = getPatchUpdatesUseCase().available
			updateUiState {
				copy(
					isPatchEnabled = updatesAvailable || !isPatched(),
					patchUpdatesAvailable = updatesAvailable,
					checkedForPatchUpdates = true
				)
			}
		}
	}

	fun patchUpdatesMessageShown() = updateUiState {
		copy(patchUpdatesMessageShown = true)
	}

	private fun updateUiState(reduce: HomeUiState.() -> HomeUiState) {
		_uiState.value = _uiState.value.reduce()
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)
}