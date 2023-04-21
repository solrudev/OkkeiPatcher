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
import ru.solrudev.okkeipatcher.data.service.*
import ru.solrudev.okkeipatcher.data.service.apksigner.ApkSigner
import ru.solrudev.okkeipatcher.data.service.apksigner.ApkSignerWrapper
import ru.solrudev.okkeipatcher.domain.service.StorageChecker

@InstallIn(SingletonComponent::class)
@Module
interface ServiceBindModule {

	@Binds
	fun bindGameInstallationProvider(gameInstallationProvider: GameInstallationProviderImpl): GameInstallationProvider

	@Binds
	fun bindStorageChecker(storageChecker: StorageCheckerImpl): StorageChecker

	@Binds
	fun bindApkSigner(apkSigner: ApkSignerWrapper): ApkSigner

	@Binds
	fun bindFileDownloader(fileDownloader: FileDownloaderImpl): FileDownloader
}