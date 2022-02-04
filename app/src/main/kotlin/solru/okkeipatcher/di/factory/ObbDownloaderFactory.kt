package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.domain.model.files.common.CommonFiles
import solru.okkeipatcher.domain.services.ObbDownloader
import solru.okkeipatcher.repository.patch.ObbDataRepository

@AssistedFactory
interface ObbDownloaderFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloader
}