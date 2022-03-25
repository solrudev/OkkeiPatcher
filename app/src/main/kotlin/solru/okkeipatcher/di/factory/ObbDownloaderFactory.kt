package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.repository.patch.ObbDataRepository
import solru.okkeipatcher.domain.service.ObbDownloader

@AssistedFactory
interface ObbDownloaderFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloader
}