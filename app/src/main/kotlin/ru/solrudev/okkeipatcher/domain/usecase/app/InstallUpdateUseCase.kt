package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

class InstallUpdateUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	suspend operator fun invoke() = okkeiPatcherRepository.installUpdate()
}