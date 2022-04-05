package ru.solrudev.okkeipatcher.domain.usecase.app.impl

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.usecase.app.GetAppUpdateFileUseCase
import javax.inject.Inject

class GetAppUpdateFileUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateFileUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getUpdateFile().invoke()
}