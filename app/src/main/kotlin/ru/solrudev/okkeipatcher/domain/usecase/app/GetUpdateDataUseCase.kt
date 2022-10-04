package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface GetUpdateDataUseCase {
	suspend operator fun invoke(refresh: Boolean): OkkeiPatcherUpdateData
}

class GetUpdateDataUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : GetUpdateDataUseCase {

	override suspend fun invoke(refresh: Boolean) = okkeiPatcherRepository.getUpdateData(refresh)
}