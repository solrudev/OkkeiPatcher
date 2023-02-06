package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetIsUpdateAvailableFlowUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	operator fun invoke() = okkeiPatcherRepository.isUpdateAvailable
}