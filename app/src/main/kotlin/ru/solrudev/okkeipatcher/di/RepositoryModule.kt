@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.app.repository.ConnectivityRepository
import ru.solrudev.okkeipatcher.app.repository.LicensesRepository
import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.app.repository.work.DownloadUpdateWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.HashRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.*
import ru.solrudev.okkeipatcher.data.repository.app.work.DownloadUpdateWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.work.WorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.*
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [RepositoryFlavorModule::class, PatchRepositoryModule::class])
interface RepositoryModule {

	@Binds
	@Singleton
	fun bindStateRepository(
		stateRepository: PreferencesRepository
	): PatchStateRepository

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
	fun bindObbBackupRepository(
		obbBackupRepository: ObbBackupRepositoryImpl
	): ObbBackupRepository

	@Binds
	@Singleton
	fun bindSaveDataRepository(
		saveDataRepository: SaveDataRepositoryImpl
	): SaveDataRepository

	@Binds
	@Singleton
	fun bindHashRepository(
		hashRepository: HashRepositoryImpl
	): HashRepository

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

	@Binds
	@Singleton
	fun bindDownloadUpdateWorkRepository(
		downloadUpdateWorkRepository: DownloadUpdateWorkRepositoryImpl
	): DownloadUpdateWorkRepository
}