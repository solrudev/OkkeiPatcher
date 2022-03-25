package solru.okkeipatcher.domain.usecase.app.impl

import solru.okkeipatcher.domain.repository.OkkeiPatcherRepository
import solru.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import solru.okkeipatcher.io.exception.NetworkNotAvailableException
import javax.inject.Inject

class GetIsAppUpdateAvailableUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetIsAppUpdateAvailableUseCase {

	override suspend fun invoke() = try {
		okkeiPatcherRepository.isUpdateAvailable()
	} catch (_: NetworkNotAvailableException) {
		false
	}
}