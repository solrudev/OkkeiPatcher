package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.GetPatchWorkUseCase
import javax.inject.Inject

class GetPatchWorkUseCaseImpl @Inject constructor(private val patchWorkRepository: PatchWorkRepository) :
	GetPatchWorkUseCase {

	override fun invoke() = patchWorkRepository.getPatchWork()
}