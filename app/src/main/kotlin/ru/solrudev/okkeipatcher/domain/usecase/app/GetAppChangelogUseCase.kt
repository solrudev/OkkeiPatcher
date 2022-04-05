package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto

interface GetAppChangelogUseCase {
	suspend operator fun invoke(): OkkeiPatcherChangelogDto
}