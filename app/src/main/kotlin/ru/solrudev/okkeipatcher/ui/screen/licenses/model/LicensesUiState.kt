package ru.solrudev.okkeipatcher.ui.screen.licenses.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.domain.model.License

data class LicensesUiState(
	val licenses: List<License> = emptyList()
) : JetState