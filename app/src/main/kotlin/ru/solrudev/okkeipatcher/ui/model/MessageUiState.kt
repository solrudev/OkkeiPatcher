package ru.solrudev.okkeipatcher.ui.model

import io.github.solrudev.jetmvi.UiState
import ru.solrudev.okkeipatcher.domain.core.Message

data class MessageUiState(
	val isVisible: Boolean = false,
	val data: Message = Message.empty
) : UiState

val MessageUiState.shouldShow: Boolean
	get() = !isVisible && data != Message.empty