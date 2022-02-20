package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.OkkeiPatcherChangelog

interface GetAppChangelogUseCase {
	suspend operator fun invoke(): OkkeiPatcherChangelog
}