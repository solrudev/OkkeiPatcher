package ru.solrudev.okkeipatcher.ui.navhost.view

import android.content.Context
import android.graphics.Color
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.util.getMaterialColor

class UpdateBadgeView(private val binding: OkkeiNavHostBinding) : JetView<NavHostUiState> {

	private val context: Context
		get() = binding.root.context

	override val trackedState = listOf(NavHostUiState::isUpdateAvailable)

	override fun render(uiState: NavHostUiState) {
		displayUpdateBadge(uiState.isUpdateAvailable)
	}

	private fun displayUpdateBadge(isUpdateAvailable: Boolean) = with(binding) {
		if (isUpdateAvailable) {
			val color = context.getMaterialColor(com.google.android.material.R.attr.colorError, Color.RED)
			bottomNavigationViewNavHost?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
			navigationRailViewNavHost?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
		} else {
			bottomNavigationViewNavHost?.removeBadge(R.id.update_fragment)
			navigationRailViewNavHost?.removeBadge(R.id.update_fragment)
		}
	}
}