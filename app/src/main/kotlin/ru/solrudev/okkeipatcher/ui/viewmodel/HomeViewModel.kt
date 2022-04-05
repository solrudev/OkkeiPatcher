package ru.solrudev.okkeipatcher.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.domain.AppKey
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.ui.model.HomeUiState
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getGetIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCaseProvider: Provider<GetPatchUpdatesUseCase>
) : ViewModel(), DefaultLifecycleObserver {

	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()

	override fun onCreate(owner: LifecycleOwner) = _uiState.update {
		it.copy(
			isPatchEnabled = !isPatched() || it.patchUpdatesAvailable,
			isRestoreEnabled = isPatched()
		)
	}

	fun checkPatchUpdates() {
		viewModelScope.launch {
			val getPatchUpdatesUseCase = getPatchUpdatesUseCaseProvider.get()
			val updatesAvailable = getPatchUpdatesUseCase().available
			_uiState.update {
				it.copy(
					isPatchEnabled = updatesAvailable || !isPatched(),
					patchUpdatesAvailable = updatesAvailable,
					checkedForPatchUpdates = true
				)
			}
		}
	}

	fun patchUpdatesMessageShown() = _uiState.update {
		it.copy(patchUpdatesMessageShown = true)
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)
}