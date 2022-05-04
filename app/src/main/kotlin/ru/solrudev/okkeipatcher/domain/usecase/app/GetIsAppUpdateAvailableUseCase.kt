package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException
import javax.inject.Inject

interface GetIsAppUpdateAvailableUseCase {
	suspend operator fun invoke(): Boolean
}

class GetIsAppUpdateAvailableUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetIsAppUpdateAvailableUseCase {

	override suspend fun invoke() = try {
		okkeiPatcherRepository.isUpdateAvailable()
	} catch (_: NetworkNotAvailableException) {
		false
	}
}