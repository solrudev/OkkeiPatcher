@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.database.WorkDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

	@Provides
	@Singleton
	fun provideWorkDatabase(@ApplicationContext applicationContext: Context): WorkDatabase {
		return Room.databaseBuilder(applicationContext, WorkDatabase::class.java, "work.db").build()
	}

	@Provides
	@Singleton
	fun provideWorkDao(database: WorkDatabase) = database.workDao()
}