package ru.solrudev.okkeipatcher.domain.usecase.patch.factory

import ru.solrudev.okkeipatcher.domain.factory.Factory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import javax.inject.Inject
import javax.inject.Provider

interface GetPatchUpdatesUseCaseFactory : Factory<GetPatchUpdatesUseCase>

class GetPatchUpdatesUseCaseFactoryImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val getPatchUpdatesUseCases: Map<Language, @JvmSuppressWildcards Provider<GetPatchUpdatesUseCase>>,
) : GetPatchUpdatesUseCaseFactory {

	override suspend fun create(): GetPatchUpdatesUseCase {
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		return getPatchUpdatesUseCases.getValue(patchLanguage).get()
	}
}