package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

class GetPendingUpdateDownloadWorkFlowUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	operator fun invoke() = okkeiPatcherRepository.getPendingUpdateDownloadWorkFlow()
}