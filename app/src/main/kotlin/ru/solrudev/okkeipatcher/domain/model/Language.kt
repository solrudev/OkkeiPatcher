package ru.solrudev.okkeipatcher.domain.model

enum class Language {
	English;

	companion object {
		fun fromString(languageName: String?): Language {
			if (languageName == null) {
				return English
			}
			return valueOf(languageName)
		}
	}
}