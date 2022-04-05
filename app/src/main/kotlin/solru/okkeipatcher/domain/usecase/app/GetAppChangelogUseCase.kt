package solru.okkeipatcher.domain.usecase.app

import android.content.Context
import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto

interface GetAppChangelogUseCase {
	suspend operator fun invoke(context: Context): OkkeiPatcherChangelogDto
}