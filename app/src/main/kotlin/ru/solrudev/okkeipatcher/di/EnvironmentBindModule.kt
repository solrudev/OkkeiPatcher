package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.OkkeiEnvironmentImpl

@InstallIn(SingletonComponent::class)
@Module
interface EnvironmentBindModule {

	@Binds
	@Reusable
	fun bindOkkeiEnvironment(okkeiEnvironment: OkkeiEnvironmentImpl): OkkeiEnvironment
}