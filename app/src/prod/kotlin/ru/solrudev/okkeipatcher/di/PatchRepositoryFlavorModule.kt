@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository

@InstallIn(SingletonComponent::class)
@Module()
interface PatchRepositoryFlavorModule {

	@[Binds IntoMap]
	@LanguageKey(Language.English)
	fun bindDefaultPatchRepositoryIntoMap(defaultPatchRepository: DefaultPatchRepository): PatchRepository
}