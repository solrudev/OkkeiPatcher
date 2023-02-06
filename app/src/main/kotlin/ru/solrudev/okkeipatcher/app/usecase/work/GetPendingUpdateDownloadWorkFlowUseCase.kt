package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetPendingUpdateDownloadWorkFlowUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	operator fun invoke() = okkeiPatcherRepository.getPendingUpdateDownloadWorkFlow()
}