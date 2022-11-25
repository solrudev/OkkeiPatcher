package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

class GetIsUpdateAvailableFlowUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	operator fun invoke() = okkeiPatcherRepository.isUpdateAvailable
}