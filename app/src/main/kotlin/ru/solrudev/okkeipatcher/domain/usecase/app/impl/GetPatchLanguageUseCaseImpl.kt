package ru.solrudev.okkeipatcher.domain.usecase.app.impl

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.usecase.app.GetPatchLanguageUseCase
import javax.inject.Inject

class GetPatchLanguageUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetPatchLanguageUseCase {

	override suspend fun invoke() = preferencesRepository.patchLanguageDao.retrieve()
}