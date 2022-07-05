package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.data.repository.patch.DefaultPatchRepositoryImpl
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface PatchRepositoryModule {

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindDefaultPatchRepository(
		defaultPatchRepository: DefaultPatchRepository
	): PatchRepository

	@Binds
	@Singleton
	fun bindDefaultPatchRepository(
		defaultPatchRepository: DefaultPatchRepositoryImpl
	): DefaultPatchRepository
}