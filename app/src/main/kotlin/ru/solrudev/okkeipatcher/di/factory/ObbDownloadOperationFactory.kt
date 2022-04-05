package ru.solrudev.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.operation.ObbDownloadOperation
import ru.solrudev.okkeipatcher.domain.repository.patch.ObbDataRepository

@AssistedFactory
interface ObbDownloadOperationFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloadOperation
}