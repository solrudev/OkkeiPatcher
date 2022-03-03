package solru.okkeipatcher.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.usecase.GetPatchUpdatesUseCase
import solru.okkeipatcher.domain.usecase.IsAppUpdateAvailableUseCase
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class MainViewModel @Inject constructor(
	private val getIsAppUpdateAvailableUseCase: IsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCaseProvider: Provider<GetPatchUpdatesUseCase>
) : ViewModel(), DefaultLifecycleObserver {

	private val _isPatchEnabled = MutableStateFlow(!isPatched())
	private val _isRestoreEnabled = MutableStateFlow(isPatched())
	private val _patchUpdatesAvailable = MutableSharedFlow<Unit>()
	val isPatchEnabled = _isPatchEnabled.asStateFlow()
	val isRestoreEnabled = _isRestoreEnabled.asStateFlow()
	val patchUpdatesAvailable = _patchUpdatesAvailable.asSharedFlow()

	fun checkPatchUpdates() {
		viewModelScope.launch {
			if (isPatchUpdateAvailable()) {
				_patchUpdatesAvailable.emit(Unit)
			}
		}
	}

	fun setIsPatched(value: Boolean) {
		_isPatchEnabled.value = !value
		_isRestoreEnabled.value = value
	}

	private suspend fun isPatchUpdateAvailable(): Boolean {
		val getPatchUpdatesUseCase = getPatchUpdatesUseCaseProvider.get()
		val updatesAvailable = getPatchUpdatesUseCase().available
		_isPatchEnabled.value = updatesAvailable || !isPatched()
		return updatesAvailable
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)
}