package solru.okkeipatcher.ui.state

import solru.okkeipatcher.data.Message

data class UiMessage(
	val isVisible: Boolean = false,
	val data: Message? = null
)