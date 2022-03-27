package solru.okkeipatcher.domain.usecase.app.impl

import solru.okkeipatcher.domain.repository.OkkeiPatcherRepository
import solru.okkeipatcher.domain.usecase.app.GetAppUpdateFileUseCase
import javax.inject.Inject

class GetAppUpdateFileUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateFileUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getUpdateFile().invoke()
}