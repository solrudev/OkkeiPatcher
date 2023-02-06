package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import javax.inject.Inject

class InstallUpdateUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	suspend operator fun invoke() = okkeiPatcherRepository.installUpdate()
}