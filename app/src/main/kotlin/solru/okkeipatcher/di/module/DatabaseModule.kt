package solru.okkeipatcher.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.data.db.OkkeiDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
		applicationContext,
		OkkeiDatabase::class.java,
		"okkei.db"
	).build()

	@Provides
	@Singleton
	fun provideWorkDao(database: OkkeiDatabase) = database.workDao()
}