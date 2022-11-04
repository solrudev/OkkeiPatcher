package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model

import io.github.solrudev.jetmvi.UiState
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.EmptyString
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.model.MessageUiState

data class ClearDataUiState(
	val warning: MessageUiState = MessageUiState(
		data = Message(
			LocalizedString.resource(R.string.warning_clear_data_title),
			LocalizedString.resource(R.string.warning_clear_data)
		)
	),
	val isCleared: Boolean = false,
	val error: LocalizedString = LocalizedString.empty(),
	val canShowErrorMessage: Boolean = true
) : UiState

val ClearDataUiState.shouldShowErrorMessage: Boolean
	get() = error !is EmptyString && canShowErrorMessage