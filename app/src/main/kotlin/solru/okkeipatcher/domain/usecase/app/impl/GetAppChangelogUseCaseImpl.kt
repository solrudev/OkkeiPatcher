package solru.okkeipatcher.domain.usecase.app.impl

import androidx.core.os.ConfigurationCompat
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.repository.OkkeiPatcherRepository
import solru.okkeipatcher.domain.usecase.app.GetAppChangelogUseCase
import javax.inject.Inject

class GetAppChangelogUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppChangelogUseCase {

	override suspend fun invoke(): OkkeiPatcherChangelogDto {
		val locale = ConfigurationCompat.getLocales(OkkeiApplication.context.resources.configuration)[0]
		return okkeiPatcherRepository.getChangelog(locale)
	}
}