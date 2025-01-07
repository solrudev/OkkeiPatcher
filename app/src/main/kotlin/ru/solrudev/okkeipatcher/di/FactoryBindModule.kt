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
import ru.solrudev.okkeipatcher.data.operation.factory.ObbPatchOperationFactoryImpl
import ru.solrudev.okkeipatcher.data.operation.factory.ScriptsPatchOperationFactoryImpl
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactoryImpl
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactoryImpl
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactoryImpl
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.operation.factory.ScriptsPatchOperationFactory

@InstallIn(SingletonComponent::class)
@Module
interface FactoryBindModule {

	@Binds
	fun bindScriptsPatchOperationFactory(
		scriptsPatchOperationFactory: ScriptsPatchOperationFactoryImpl
	): ScriptsPatchOperationFactory

	@Binds
	fun bindObbDownloadOperationFactory(
		obbDownloadOperationFactory: ObbPatchOperationFactoryImpl
	): ObbPatchOperationFactory

	@Binds
	fun bindNotificationServiceFactory(
		notificationServiceFactory: NotificationServiceFactoryImpl
	): NotificationServiceFactory

	@Binds
	fun bindApkZipPackageFactory(
		apkZipPackageFactory: ApkZipPackageFactoryImpl
	): ApkZipPackageFactory

	@Binds
	fun bindPreferencesDataStoreFactory(
		preferencesDataStoreFactory: PreferencesDataStoreFactoryImpl
	): PreferencesDataStoreFactory
}