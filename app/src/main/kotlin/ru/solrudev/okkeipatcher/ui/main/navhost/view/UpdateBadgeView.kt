package ru.solrudev.okkeipatcher.ui.main.navhost.view

import android.content.Context
import android.graphics.Color
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentMainBinding
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import ru.solrudev.okkeipatcher.ui.main.util.getMaterialColor

class UpdateBadgeView(private val binding: FragmentMainBinding) : JetView<MainUiState> {

	private val context: Context
		get() = binding.root.context

	override fun render(uiState: MainUiState) {
		displayUpdateBadge(uiState.isUpdateAvailable)
	}

	private fun displayUpdateBadge(isUpdateAvailable: Boolean) = with(binding) {
		if (isUpdateAvailable) {
			val color = context.getMaterialColor(com.google.android.material.R.attr.colorError, Color.RED)
			bottomNavigationViewMain?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
			navigationRailViewMain?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
		} else {
			bottomNavigationViewMain?.removeBadge(R.id.update_fragment)
			navigationRailViewMain?.removeBadge(R.id.update_fragment)
		}
	}
}