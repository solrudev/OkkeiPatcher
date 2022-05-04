package ru.solrudev.okkeipatcher.domain.usecase.patch.factory

import ru.solrudev.okkeipatcher.domain.factory.Factory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import javax.inject.Inject
import javax.inject.Provider

interface GetPatchSizeInMbUseCaseFactory : Factory<GetPatchSizeInMbUseCase>

class GetPatchSizeInMbUseCaseFactoryImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val getPatchSizeInMbUseCases: Map<Language, @JvmSuppressWildcards Provider<GetPatchSizeInMbUseCase>>,
) : GetPatchSizeInMbUseCaseFactory {

	override suspend fun create(): GetPatchSizeInMbUseCase {
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		return getPatchSizeInMbUseCases.getValue(patchLanguage).get()
	}
}