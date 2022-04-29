package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.model.Language

interface PreferencesRepository {
	suspend fun getIsPatched(): Boolean
	suspend fun getHandleSaveData(): Boolean
	suspend fun getPatchLanguage(): Language
	suspend fun setIsPatched(isPatched: Boolean)
	suspend fun setHandleSaveData(handleSaveData: Boolean)
	suspend fun setPatchLanguage(language: Language)
}