package solru.okkeipatcher.domain.usecase.impl

import solru.okkeipatcher.domain.usecase.IsAppUpdateAvailableUseCase
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import javax.inject.Inject

class IsAppUpdateAvailableUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	IsAppUpdateAvailableUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.isUpdateAvailable()
}