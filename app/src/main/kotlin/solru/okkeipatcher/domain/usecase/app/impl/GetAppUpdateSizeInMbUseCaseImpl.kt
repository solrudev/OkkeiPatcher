package solru.okkeipatcher.domain.usecase.app.impl

import solru.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import solru.okkeipatcher.domain.usecase.app.GetAppUpdateSizeInMbUseCase
import javax.inject.Inject

class GetAppUpdateSizeInMbUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateSizeInMbUseCase {

	override suspend fun invoke() = try {
		okkeiPatcherRepository.getUpdateSizeInMb()
	} catch (t: Throwable) {
		-1.0
	}
}