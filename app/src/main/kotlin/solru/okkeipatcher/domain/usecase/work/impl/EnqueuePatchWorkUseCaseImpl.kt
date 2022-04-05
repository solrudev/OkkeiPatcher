package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.repository.work.PatchWorkRepository
import solru.okkeipatcher.domain.usecase.work.EnqueuePatchWorkUseCase
import javax.inject.Inject

class EnqueuePatchWorkUseCaseImpl @Inject constructor(private val patchWorkRepository: PatchWorkRepository) :
	EnqueuePatchWorkUseCase {

	override suspend fun invoke() = patchWorkRepository.enqueuePatchWork()
}