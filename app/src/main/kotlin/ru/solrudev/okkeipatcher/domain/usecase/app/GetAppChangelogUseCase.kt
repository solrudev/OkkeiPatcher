//TODO

package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface GetAppChangelogUseCase {
	suspend operator fun invoke(): List<OkkeiPatcherVersion>
}

class GetAppChangelogUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : GetAppChangelogUseCase {

	override suspend fun invoke() = okkeiPatcherRepository.getChangelog()
}