package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.ProgressData

data class WorkUiState(
	val isLoading: Boolean = false,
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val isButtonEnabled: Boolean = false,
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val startWorkMessage: MessageUiState = MessageUiState(),
	val cancelWorkMessage: MessageUiState = MessageUiState(),
	val errorMessage: MessageUiState = MessageUiState()
)

val WorkUiState.isWorkFinished: Boolean
	get() = isWorkSuccessful || isWorkCanceled || errorMessage.data != null