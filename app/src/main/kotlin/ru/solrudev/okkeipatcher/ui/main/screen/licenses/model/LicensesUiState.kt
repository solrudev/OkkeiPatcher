package ru.solrudev.okkeipatcher.ui.main.screen.licenses.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.app.model.License

data class LicensesUiState(
	val licenses: List<License> = emptyList()
) : JetState