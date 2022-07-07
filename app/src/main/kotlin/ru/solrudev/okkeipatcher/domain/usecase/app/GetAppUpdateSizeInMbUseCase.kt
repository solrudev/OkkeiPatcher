// TODO

package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface GetAppUpdateSizeInMbUseCase {
	suspend operator fun invoke(): Double
}

class GetAppUpdateSizeInMbUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateSizeInMbUseCase {

	override suspend fun invoke() = try {
		okkeiPatcherRepository.getUpdateSizeInMb()
	} catch (t: Throwable) {
		-1.0
	}
}