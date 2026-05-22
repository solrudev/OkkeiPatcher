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

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import ru.solrudev.okkeipatcher.app.repository.OperationModeRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.filesystem.FileSystemManagerProvider
import ru.solrudev.okkeipatcher.data.filesystem.OperationModeAwareFileSystem
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalFileSystem

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultFileSystem

@InstallIn(SingletonComponent::class)
@Module
object FileSystemModule {

	@DefaultFileSystem
	@Provides
	@Singleton
	fun provideDefaultFileSystem(operationModeAwareFileSystem: OperationModeAwareFileSystem): FileSystem {
		return operationModeAwareFileSystem
	}

	@LocalFileSystem
	@Provides
	fun provideLocalFileSystem(): FileSystem {
		return FileSystem.SYSTEM
	}

	@Provides
	@Singleton
	fun provideFileSystemManagerProvider(
		preferencesRepository: PreferencesRepository,
		operationModeRepository: OperationModeRepository,
		@IoDispatcher ioDispatcher: CoroutineDispatcher,
		@MainDispatcher mainDispatcher: CoroutineDispatcher,
		@ApplicationContext context: Context
	) = FileSystemManagerProvider(
		preferencesRepository.operationMode.flow,
		operationModeRepository,
		ioDispatcher,
		mainDispatcher,
		context.packageName
	)
}