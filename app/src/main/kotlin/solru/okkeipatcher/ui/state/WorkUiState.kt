package solru.okkeipatcher.ui.state

import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.ProgressData

data class WorkUiState(
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val startWorkMessage: Message? = null,
	val cancelWorkMessage: Message? = null,
	val errorMessage: Message? = null,
	val isStartWorkMessageVisible: Boolean = false,
	val isCancelWorkMessageVisible: Boolean = false,
	val isErrorMessageVisible: Boolean = false
)