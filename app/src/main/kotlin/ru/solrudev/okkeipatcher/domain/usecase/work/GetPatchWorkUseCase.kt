package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import javax.inject.Inject

interface GetPatchWorkUseCase {

	/**
	 * @return patch work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}

class GetPatchWorkUseCaseImpl @Inject constructor(private val patchWorkRepository: PatchWorkRepository) :
	GetPatchWorkUseCase {

	override fun invoke() = patchWorkRepository.getPatchWork()
}