package solru.okkeipatcher.model.dto

import solru.okkeipatcher.model.LocalizedString
import java.io.Serializable

data class Message(
	val title: LocalizedString,
	val message: LocalizedString
) : Serializable