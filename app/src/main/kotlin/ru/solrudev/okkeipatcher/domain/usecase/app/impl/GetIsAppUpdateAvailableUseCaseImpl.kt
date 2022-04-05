package ru.solrudev.okkeipatcher.domain.usecase.app.impl

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException
import javax.inject.Inject

class GetIsAppUpdateAvailableUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetIsAppUpdateAvailableUseCase {

	override suspend fun invoke() = try {
		okkeiPatcherRepository.isUpdateAvailable()
	} catch (_: NetworkNotAvailableException) {
		false
	}
}