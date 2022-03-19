package solru.okkeipatcher.ui.state

import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.ProgressData

data class WorkUiState(
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val startWorkMessage: UiMessage = UiMessage(),
	val cancelWorkMessage: UiMessage = UiMessage(),
	val errorMessage: UiMessage = UiMessage()
)