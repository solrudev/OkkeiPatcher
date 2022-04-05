package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.GetRestoreWorkUseCase
import javax.inject.Inject

class GetRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	GetRestoreWorkUseCase {

	override fun invoke() = restoreWorkRepository.getRestoreWork()
}