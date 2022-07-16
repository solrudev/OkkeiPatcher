package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.UiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState

data class SaveDataAccessUiState(
	val rationale: MessageUiState = MessageUiState(
		data = Message(
			LocalizedString.resource(R.string.rationale_save_data_access_permission_title),
			LocalizedString.resource(R.string.rationale_save_data_access_permission)
		)
	),
	val handleSaveDataEnabled: Boolean = false
) : UiState