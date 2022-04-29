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
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsPatchedUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetPatchLanguageUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.ui.model.HomeUiState
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getGetIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCase,
	private val getPatchUpdatesUseCases: Map<Language, @JvmSuppressWildcards Provider<GetPatchUpdatesUseCase>>,
	private val getPatchLanguageUseCase: GetPatchLanguageUseCase,
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
			val patchLanguage = getPatchLanguageUseCase()
			val getPatchUpdatesUseCase = getPatchUpdatesUseCases.getValue(patchLanguage).get()
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