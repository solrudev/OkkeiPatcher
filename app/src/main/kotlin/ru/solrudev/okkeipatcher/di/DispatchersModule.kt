@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
object DispatchersModule {

	@IoDispatcher
	@Provides
	fun provideIoDispatcher() = Dispatchers.IO
}