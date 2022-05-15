package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.ui.core.UiState

data class MessageUiState(
	val isVisible: Boolean = false,
	val data: Message = Message.empty
) : UiState

val MessageUiState.shouldShow: Boolean
	get() = !isVisible && data != Message.empty