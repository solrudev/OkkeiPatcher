package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

class GetUpdateDataUseCase @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) {
	suspend operator fun invoke(refresh: Boolean) = okkeiPatcherRepository.getUpdateData(refresh)
}