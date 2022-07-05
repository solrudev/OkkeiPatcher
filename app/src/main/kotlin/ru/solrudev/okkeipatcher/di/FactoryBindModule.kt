package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.service.operation.factory.ScriptsPatchOperationFactoryImpl
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactoryImpl
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategyFactoryImpl
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactory
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactoryImpl
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ScriptsPatchOperationFactory

@InstallIn(SingletonComponent::class)
@Module
interface FactoryBindModule {

	@Binds
	fun bindScriptsPatchOperationFactory(
		scriptsPatchOperationFactory: ScriptsPatchOperationFactoryImpl
	): ScriptsPatchOperationFactory

	@Binds
	fun bindObbDownloadOperationFactory(
		obbDownloadOperationFactory: ObbDownloadOperationFactoryImpl
	): ObbDownloadOperationFactory

	@Binds
	fun bindPatchRepositoryFactory(
		patchRepositoryFactory: PatchRepositoryFactoryImpl
	): PatchRepositoryFactory

	@Binds
	fun bindGameFileStrategyFactory(
		gameFileStrategyFactory: GameFileStrategyFactoryImpl
	): GameFileStrategyFactory
}