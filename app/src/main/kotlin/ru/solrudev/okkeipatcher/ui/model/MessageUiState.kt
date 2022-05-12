package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Message

data class MessageUiState(
	val isVisible: Boolean = false,
	val data: Message = Message.empty
)

val MessageUiState.shouldShow: Boolean
	get() = !isVisible && data != Message.empty