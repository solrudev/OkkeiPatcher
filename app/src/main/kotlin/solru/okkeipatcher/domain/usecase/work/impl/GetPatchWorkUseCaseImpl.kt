package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.repository.work.PatchWorkRepository
import solru.okkeipatcher.domain.usecase.work.GetPatchWorkUseCase
import javax.inject.Inject

class GetPatchWorkUseCaseImpl @Inject constructor(private val patchWorkRepository: PatchWorkRepository) :
	GetPatchWorkUseCase {

	override fun invoke() = patchWorkRepository.getPatchWork()
}