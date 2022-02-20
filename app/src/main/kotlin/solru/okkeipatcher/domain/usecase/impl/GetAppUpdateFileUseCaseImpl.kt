package solru.okkeipatcher.domain.usecase.impl

import solru.okkeipatcher.domain.usecase.GetAppUpdateFileUseCase
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetAppUpdateFileUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateFileUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getUpdateFile()
}