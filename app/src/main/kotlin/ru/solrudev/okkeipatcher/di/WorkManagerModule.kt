@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object WorkManagerModule {

	@Provides
	fun provideWorkManager(@ApplicationContext applicationContext: Context): WorkManager {
		return WorkManager.getInstance(applicationContext)
	}
}