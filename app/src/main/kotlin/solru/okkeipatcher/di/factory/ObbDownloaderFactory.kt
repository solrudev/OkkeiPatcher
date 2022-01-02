package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.services.ObbDownloader
import solru.okkeipatcher.repository.patch.ObbDataRepository

@AssistedFactory
interface ObbDownloaderFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloader
}