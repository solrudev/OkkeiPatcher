// TODO

package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import java.io.File
import javax.inject.Inject

interface GetAppUpdateFileUseCase {
	suspend operator fun invoke(): File
}

class GetAppUpdateFileUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppUpdateFileUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getUpdateFile().invoke()
}