package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactoryImpl

@InstallIn(SingletonComponent::class)
@Module
interface PatchRepositoryModule {

	@Binds
	fun bindPatchRepositoryFactory(patchRepositoryFactory: PatchRepositoryFactoryImpl): PatchRepositoryFactory

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindDefaultPatchRepository(defaultPatchRepository: DefaultPatchRepository): PatchRepository
}