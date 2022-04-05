package solru.okkeipatcher.domain.usecase.app.impl

import android.content.Context
import androidx.core.os.ConfigurationCompat
import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import solru.okkeipatcher.domain.usecase.app.GetAppChangelogUseCase
import javax.inject.Inject

class GetAppChangelogUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppChangelogUseCase {

	override suspend fun invoke(context: Context): OkkeiPatcherChangelogDto {
		val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
		return okkeiPatcherRepository.getChangelog(locale)
	}
}