package ru.solrudev.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.app.ConnectivityRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.OkkeiPatcherRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.PreferencesRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.patch.DefaultPatchRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.PatchWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.RestoreWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.WorkRepositoryImpl
import ru.solrudev.okkeipatcher.domain.repository.app.ConnectivityRepository
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryFlavorModule {

	@Binds
	@Singleton
	fun bindPatchWorkRepository(patchWorkRepository: PatchWorkRepositoryImpl): PatchWorkRepository

	@Binds
	@Singleton
	fun bindRestoreWorkRepository(restoreWorkRepository: RestoreWorkRepositoryImpl): RestoreWorkRepository
}