package ru.solrudev.okkeipatcher.domain.repository.patch.factory

import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Provider

interface PatchRepositoryFactory : SuspendFactory<PatchRepository>

class PatchRepositoryFactoryImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val patchRepositories: Map<Language, @JvmSuppressWildcards Provider<PatchRepository>>
) : PatchRepositoryFactory {

	override suspend fun create(): PatchRepository {
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		return patchRepositories.getValue(patchLanguage).get()
	}
}