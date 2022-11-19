package ru.solrudev.okkeipatcher.di

import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.gamefile.SaveDataDocumentFile
import ru.solrudev.okkeipatcher.data.repository.gamefile.SaveDataFile
import ru.solrudev.okkeipatcher.data.repository.gamefile.SaveDataRawFile
import javax.inject.Provider

@InstallIn(SingletonComponent::class)
@Module
object SaveDataFileModule {

	@Provides
	fun provideSaveDataFile(
		rawFile: Provider<SaveDataRawFile>,
		documentFile: Provider<SaveDataDocumentFile>
	): SaveDataFile {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			documentFile.get()
		} else {
			rawFile.get()
		}
	}
}