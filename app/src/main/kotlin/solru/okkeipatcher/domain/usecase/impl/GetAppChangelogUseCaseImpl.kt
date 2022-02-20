package solru.okkeipatcher.domain.usecase.impl

import androidx.core.os.ConfigurationCompat
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.OkkeiPatcherChangelog
import solru.okkeipatcher.domain.usecase.GetAppChangelogUseCase
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import javax.inject.Inject

class GetAppChangelogUseCaseImpl @Inject constructor(private val okkeiPatcherRepository: OkkeiPatcherRepository) :
	GetAppChangelogUseCase {

	override suspend fun invoke(): OkkeiPatcherChangelog {
		val locale = ConfigurationCompat.getLocales(OkkeiApplication.context.resources.configuration)[0]
		return okkeiPatcherRepository.getChangelog(locale)
	}
}