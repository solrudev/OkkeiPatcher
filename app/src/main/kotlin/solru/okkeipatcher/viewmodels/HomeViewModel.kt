package solru.okkeipatcher.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.usecase.GetPatchUpdatesUseCase
import solru.okkeipatcher.domain.usecase.IsAppUpdateAvailableUseCase
import solru.okkeipatcher.ui.state.HomeUiState
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getIsAppUpdateAvailableUseCase: IsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCaseProvider: Provider<GetPatchUpdatesUseCase>
) : ViewModel(), DefaultLifecycleObserver {

	private val _uiState = MutableStateFlow(
		HomeUiState(
			isPatchEnabled = !isPatched(),
			isRestoreEnabled = isPatched()
		)
	)

	val uiState = _uiState.asStateFlow()

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

	fun setIsPatched(value: Boolean) = updateUiState {
		copy(
			isPatchEnabled = !value,
			isRestoreEnabled = value
		)
	}

	fun patchUpdatesMessageShown() = updateUiState {
		copy(patchUpdatesAvailable = false)
	}

	private fun updateUiState(reduce: HomeUiState.() -> HomeUiState) {
		_uiState.value = _uiState.value.reduce()
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)
}