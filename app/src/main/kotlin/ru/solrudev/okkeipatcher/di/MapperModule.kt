package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.work.mapper.WorkStateMapper
import ru.solrudev.okkeipatcher.data.worker.WorkStateMapperImpl

@InstallIn(SingletonComponent::class)
@Module
interface MapperModule {

	@Binds
	fun bindWorkStateMapper(workStateMapper: WorkStateMapperImpl): WorkStateMapper
}