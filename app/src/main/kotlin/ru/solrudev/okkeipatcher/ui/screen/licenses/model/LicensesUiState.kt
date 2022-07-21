package ru.solrudev.okkeipatcher.ui.screen.licenses.model

import ru.solrudev.okkeipatcher.domain.model.License
import ru.solrudev.okkeipatcher.ui.core.UiState

data class LicensesUiState(
	val licenses: List<License> = emptyList()
) : UiState