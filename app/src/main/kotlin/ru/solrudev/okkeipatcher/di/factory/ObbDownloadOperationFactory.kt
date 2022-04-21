package ru.solrudev.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.ObbDataRepository
import ru.solrudev.okkeipatcher.domain.service.operation.ObbDownloadOperation

@AssistedFactory
interface ObbDownloadOperationFactory {
	fun create(obbDataRepository: ObbDataRepository, commonFiles: CommonFiles): ObbDownloadOperation
}