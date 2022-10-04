package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface EnqueueUpdateDownloadWorkUseCase {
	suspend operator fun invoke(): Work
}

class EnqueueUpdateDownloadWorkUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : EnqueueUpdateDownloadWorkUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.enqueueUpdateDownloadWork()
}