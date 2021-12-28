package solru.okkeipatcher.data

import java.io.Serializable

data class Message(
	val title: LocalizedString,
	val message: LocalizedString
) : Serializable