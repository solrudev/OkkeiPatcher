package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.repository.work.RestoreWorkRepository
import javax.inject.Inject

class EnqueueRestoreWorkUseCase @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) {
	suspend operator fun invoke() = restoreWorkRepository.enqueueWork()
}