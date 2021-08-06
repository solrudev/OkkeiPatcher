package solru.okkeipatcher.model.dto

import solru.okkeipatcher.utils.extensions.empty

data class Message(
	val titleId: Int,
	val messageId: Int,
	val positiveButtonTextId: Int,
	val negativeButtonTextId: Int = 0,
	val error: String = String.empty
)