/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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
import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.app.repository.work.DownloadUpdateWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.HashRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.ConnectivityRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.LicensesRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.OkkeiPatcherRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.PermissionsRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.PreferencesRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.work.DownloadUpdateWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.app.work.WorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ApkBackupRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ApkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ObbBackupRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.ObbRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.gamefile.SaveDataRepositoryImpl
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository

@InstallIn(SingletonComponent::class)
@Module(includes = [UniqueWorkRepositoryModule::class])
interface RepositoryModule {

	@Binds
	fun bindStateRepository(stateRepository: PreferencesRepository): PatchStateRepository

	@Binds
	fun bindWorkRepository(workRepository: WorkRepositoryImpl): WorkRepository

	@Binds
	fun bindConnectivityRepository(connectivityRepository: ConnectivityRepositoryImpl): ConnectivityRepository

	@Binds
	fun bindPreferencesRepository(preferencesRepository: PreferencesRepositoryImpl): PreferencesRepository

	@Binds
	fun bindApkRepository(apkRepository: ApkRepositoryImpl): ApkRepository

	@Binds
	fun bindApkBackupRepository(apkBackupRepository: ApkBackupRepositoryImpl): ApkBackupRepository

	@Binds
	fun bindObbRepository(obbRepository: ObbRepositoryImpl): ObbRepository

	@Binds
	fun bindObbBackupRepository(obbBackupRepository: ObbBackupRepositoryImpl): ObbBackupRepository

	@Binds
	fun bindSaveDataRepository(saveDataRepository: SaveDataRepositoryImpl): SaveDataRepository

	@Binds
	fun bindHashRepository(hashRepository: HashRepositoryImpl): HashRepository

	@Binds
	fun bindPermissionsRepository(permissionsRepository: PermissionsRepositoryImpl): PermissionsRepository

	@Binds
	fun bindLicensesRepository(licensesRepository: LicensesRepositoryImpl): LicensesRepository

	@Binds
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: OkkeiPatcherRepositoryImpl): OkkeiPatcherRepository

	@Binds
	fun bindDownloadUpdateWorkRepository(
		downloadUpdateWorkRepository: DownloadUpdateWorkRepositoryImpl
	): DownloadUpdateWorkRepository
}