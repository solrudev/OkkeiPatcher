package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Message

data class MessageUiState(
	val isVisible: Boolean = false,
	val data: Message? = null
)