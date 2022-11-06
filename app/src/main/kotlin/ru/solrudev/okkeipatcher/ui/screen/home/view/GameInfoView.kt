package ru.solrudev.okkeipatcher.ui.screen.home.view

import android.content.Context
import io.github.solrudev.jetmvi.FeatureView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardGameInfoBinding
import ru.solrudev.okkeipatcher.ui.screen.home.model.GameUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState

class GameInfoView(private val binding: CardGameInfoBinding) : FeatureView<HomeUiState> {

	private val context: Context
		get() = binding.root.context

	init {
		loadGameInfo()
	}

	override val trackedUiState = listOf(HomeUiState::patchVersion)

	override fun render(uiState: HomeUiState) = with(binding) {
		val version = uiState.patchVersion.ifEmpty { context.getString(R.string.not_available) }
		textviewCardGamePatch.text = context.getString(R.string.card_patch_version, version)
	}

	private fun loadGameInfo() = with(binding) {
		val gameUiState = GameUiState(context)
		textviewCardGameTitle.text = gameUiState.title
		textviewCardGameVersion.text = context.getString(R.string.card_game_version, gameUiState.version)
		imageviewCardGameIcon.setImageDrawable(gameUiState.icon)
	}
}