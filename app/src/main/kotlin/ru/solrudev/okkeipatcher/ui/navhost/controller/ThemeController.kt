package ru.solrudev.okkeipatcher.ui.navhost.controller

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState

class ThemeController(private val application: OkkeiApplication) : JetView<NavHostUiState> {

	override val trackedState = listOf(NavHostUiState::theme)

	override fun render(uiState: NavHostUiState) {
		application.setTheme(uiState.theme)
	}
}