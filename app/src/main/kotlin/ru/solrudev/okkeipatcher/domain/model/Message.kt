package ru.solrudev.okkeipatcher.domain.model

data class Message(
	val title: LocalizedString,
	val message: LocalizedString
) {
	companion object {
		val empty = Message(LocalizedString.empty(), LocalizedString.empty())
	}
}