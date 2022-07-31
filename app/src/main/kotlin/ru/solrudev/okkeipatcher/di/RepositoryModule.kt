package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.app.*
import ru.solrudev.okkeipatcher.data.repository.gamefile.ApkBackupRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ApkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ObbRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.SaveDataRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.WorkRepositoryImpl
import ru.solrudev.okkeipatcher.domain.repository.app.*
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [RepositoryFlavorModule::class, PatchRepositoryModule::class])
interface RepositoryModule {

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(
		okkeiPatcherRepository: OkkeiPatcherRepositoryImpl
	): OkkeiPatcherRepository

	@Binds
	@Singleton
	fun bindWorkRepository(
		workRepository: WorkRepositoryImpl
	): WorkRepository

	@Binds
	@Singleton
	fun bindConnectivityRepository(
		connectivityRepository: ConnectivityRepositoryImpl
	): ConnectivityRepository

	@Binds
	@Singleton
	fun bindPreferencesRepository(
		preferencesRepository: PreferencesRepositoryImpl
	): PreferencesRepository

	@Binds
	@Singleton
	fun bindApkRepository(
		apkRepository: ApkRepositoryImpl
	): ApkRepository

	@Binds
	@Singleton
	fun bindApkBackupRepository(
		apkBackupRepository: ApkBackupRepositoryImpl
	): ApkBackupRepository

	@Binds
	@Singleton
	fun bindObbRepository(
		obbRepository: ObbRepositoryImpl
	): ObbRepository

	@Binds
	@Singleton
	fun bindSaveDataRepository(
		saveDataRepository: SaveDataRepositoryImpl
	): SaveDataRepository

	@Binds
	@Singleton
	fun bindCommonFilesHashRepository(
		commonFilesHashRepository: CommonFilesHashRepositoryImpl
	): CommonFilesHashRepository

	@Binds
	@Singleton
	fun bindPermissionsRepository(
		permissionsRepository: PermissionsRepositoryImpl
	): PermissionsRepository

	@Binds
	@Singleton
	fun bindLicensesRepository(
		licensesRepository: LicensesRepositoryImpl
	): LicensesRepository
}