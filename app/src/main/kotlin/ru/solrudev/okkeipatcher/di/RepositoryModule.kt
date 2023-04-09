/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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