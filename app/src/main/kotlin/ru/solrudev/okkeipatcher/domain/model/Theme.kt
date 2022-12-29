package ru.solrudev.okkeipatcher.domain.model

enum class Theme {
	FollowSystem, Light, Dark;

	companion object {
		private val values = values()
		fun fromOrdinal(ordinal: Int?) = ordinal?.let(values::getOrNull) ?: FollowSystem
	}
}