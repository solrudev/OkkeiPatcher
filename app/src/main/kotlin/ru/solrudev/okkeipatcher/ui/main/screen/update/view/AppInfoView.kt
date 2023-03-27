package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionName
import ru.solrudev.okkeipatcher.databinding.CardUpdateAppInfoBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState

class AppInfoView(
	private val binding: CardUpdateAppInfoBinding
) : JetView<UpdateUiState> {

	private val context by binding.root::context

	override val trackedState = listOf(
		UpdateUiState::isUpdating,
		UpdateUiState::isDownloading,
		UpdateUiState::isInstalling,
		UpdateUiState::percentDone,
		UpdateUiState::progressData,
		UpdateUiState::updateSize
	)

	override fun render(uiState: UpdateUiState) = with(binding) {
		textviewCardAppSubtitle.text = subtitleText(uiState)
		setProgress(uiState)
	}

	private fun setProgress(uiState: UpdateUiState) = with(binding.progressiconCardAppIcon) {
		setProgressVisible(uiState.isUpdating, animate = true)
		setProgressCompat(uiState.progressData.progress, animated = true)
		max = uiState.progressData.max
		isIndeterminate = uiState.isInstalling
	}

	private fun subtitleText(uiState: UpdateUiState): String {
		val text = if (uiState.isDownloading) {
			context.getString(R.string.update_percent_done, uiState.percentDone, uiState.updateSize)
		} else {
			context.getString(R.string.card_app_version, context.versionName)
		}
		return text
	}
}