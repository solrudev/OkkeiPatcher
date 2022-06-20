package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetPatchLanguageUseCase {
	suspend operator fun invoke(): Language
}

class GetPatchLanguageUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetPatchLanguageUseCase {

	override suspend fun invoke() = preferencesRepository.patchLanguage.retrieve()
}