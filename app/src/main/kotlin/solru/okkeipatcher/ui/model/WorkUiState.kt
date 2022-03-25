package solru.okkeipatcher.ui.model

import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.ProgressData

data class WorkUiState(
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val startWorkMessage: MessageUiState = MessageUiState(),
	val cancelWorkMessage: MessageUiState = MessageUiState(),
	val errorMessage: MessageUiState = MessageUiState()
)