package solru.okkeipatcher.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
	val isPatchEnabled = _isPatchEnabled.asStateFlow()
	val isRestoreEnabled = _isRestoreEnabled.asStateFlow()

	suspend fun patchUpdatesAvailable(): Boolean {
		val getPatchUpdatesUseCase = getPatchUpdatesUseCaseProvider.get()
		val updatesAvailable = getPatchUpdatesUseCase().available
		_isPatchEnabled.value = updatesAvailable
		return updatesAvailable
	}

	fun setIsPatched(value: Boolean) {
		_isPatchEnabled.value = !value
		_isRestoreEnabled.value = value
	}

	fun patch() {
	}

	fun restore() {
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)
}