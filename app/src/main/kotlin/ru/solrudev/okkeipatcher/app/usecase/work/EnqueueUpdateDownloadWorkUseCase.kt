package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import javax.inject.Inject

class EnqueueUpdateDownloadWorkUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	suspend operator fun invoke() = okkeiPatcherRepository.enqueueUpdateDownloadWork()
}