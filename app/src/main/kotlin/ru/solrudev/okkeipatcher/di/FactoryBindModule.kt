@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.operation.factory.ObbDownloadOperationFactoryImpl
import ru.solrudev.okkeipatcher.data.operation.factory.ScriptsPatchOperationFactoryImpl
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactoryImpl
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactoryImpl
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactoryImpl
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbDownloadOperationFactory
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
		obbDownloadOperationFactory: ObbDownloadOperationFactoryImpl
	): ObbDownloadOperationFactory

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