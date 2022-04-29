package ru.solrudev.okkeipatcher.domain.model

enum class Language {
	English;

	companion object {
		fun fromString(languageName: String?) = when (languageName) {
			English.name -> English
			null -> English
			else -> throw IllegalArgumentException("Unknown language name")
		}
	}
}