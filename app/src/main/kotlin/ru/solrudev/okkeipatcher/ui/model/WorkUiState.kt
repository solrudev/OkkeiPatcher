package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData

data class WorkUiState(
	val status: LocalizedString = LocalizedString.empty(),
	val progressData: ProgressData = ProgressData(),
	val isWorkSuccessful: Boolean = false,
	val isWorkCanceled: Boolean = false,
	val cancelWorkMessage: Message = Message.empty,
	val error: Message = Message.empty,
	val animationsPlayed: Boolean = false
) : UiState