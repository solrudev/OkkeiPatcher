package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface InstallUpdateUseCase {
	suspend operator fun invoke()
}

class InstallUpdateUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : InstallUpdateUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.installUpdate()
}