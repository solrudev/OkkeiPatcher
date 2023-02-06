package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetUpdateDataUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	suspend operator fun invoke(refresh: Boolean) = okkeiPatcherRepository.getUpdateData(refresh)
}