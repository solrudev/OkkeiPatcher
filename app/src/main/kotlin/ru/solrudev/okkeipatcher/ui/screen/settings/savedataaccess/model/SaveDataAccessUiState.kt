package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model

import io.github.solrudev.jetmvi.UiState
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.model.MessageUiState

data class SaveDataAccessUiState(
	val rationale: MessageUiState = MessageUiState(
		data = Message(
			LocalizedString.resource(R.string.permission_save_data_access_title),
			LocalizedString.resource(R.string.permission_save_data_access_description)
		)
	),
	val handleSaveDataEnabled: Boolean = false
) : UiState