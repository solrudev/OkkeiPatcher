package ru.solrudev.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okio.FileSystem

@InstallIn(SingletonComponent::class)
@Module
object FileSystemModule {

	@Provides
	fun provideFileSystem(): FileSystem = FileSystem.SYSTEM
}