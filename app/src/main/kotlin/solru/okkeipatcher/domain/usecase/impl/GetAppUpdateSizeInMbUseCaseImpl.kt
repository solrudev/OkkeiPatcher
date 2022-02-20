package solru.okkeipatcher.domain.usecase.impl

import solru.okkeipatcher.domain.usecase.GetAppUpdateSizeInMbUseCase
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetAppUpdateSizeInMbUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateSizeInMbUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getUpdateSizeInMb()
}