package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import javax.inject.Inject

class EnqueuePatchWorkUseCase @Inject constructor(private val patchWorkRepository: PatchWorkRepository) {
	suspend operator fun invoke() = patchWorkRepository.enqueueWork()
}