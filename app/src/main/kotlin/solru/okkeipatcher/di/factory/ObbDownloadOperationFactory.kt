package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.operation.ObbDownloadOperation
import solru.okkeipatcher.domain.repository.patch.ObbDataRepository

@AssistedFactory
interface ObbDownloadOperationFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloadOperation
}