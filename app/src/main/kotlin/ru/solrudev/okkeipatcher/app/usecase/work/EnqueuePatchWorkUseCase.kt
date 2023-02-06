package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.repository.work.PatchWorkRepository
import javax.inject.Inject

class EnqueuePatchWorkUseCase @Inject constructor(private val patchWorkRepository: PatchWorkRepository) {
	suspend operator fun invoke() = patchWorkRepository.enqueueWork()
}