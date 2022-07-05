package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactoryImpl
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactory
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactoryImpl

@InstallIn(SingletonComponent::class)
@Module
interface FactoryBindModule {

	@Binds
	fun bindObbDownloadOperationFactory(
		obbDownloadOperationFactory: ObbDownloadOperationFactoryImpl
	): ObbDownloadOperationFactory

	@Binds
	fun bindPatchRepositoryFactory(
		patchRepositoryFactory: PatchRepositoryFactoryImpl
	): PatchRepositoryFactory
}