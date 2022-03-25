package solru.okkeipatcher.ui.model

import solru.okkeipatcher.domain.model.Message

data class MessageUiState(
	val isVisible: Boolean = false,
	val data: Message? = null
)