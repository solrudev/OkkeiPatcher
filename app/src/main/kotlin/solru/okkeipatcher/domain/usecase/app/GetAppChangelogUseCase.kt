package solru.okkeipatcher.domain.usecase.app

import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto

interface GetAppChangelogUseCase {
	suspend operator fun invoke(): OkkeiPatcherChangelogDto
}