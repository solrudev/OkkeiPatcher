package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.ProgressData

data class WorkUiState(
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val cancelWorkMessage: MessageUiState = MessageUiState(),
	val errorMessage: MessageUiState = MessageUiState(),
	val animationsPlayed: Boolean = false
) : UiState