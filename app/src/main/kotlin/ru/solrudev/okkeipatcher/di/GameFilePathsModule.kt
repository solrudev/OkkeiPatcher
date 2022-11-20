package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.gamefile.*
import ru.solrudev.okkeipatcher.data.repository.gamefile.paths.*

@InstallIn(SingletonComponent::class)
@Module
interface GameFilePathsModule {

	@Binds
	fun bindApkPaths(apkPaths: ApkPathsImpl): ApkPaths

	@Binds
	fun bindObbPaths(obbPaths: ObbPathsImpl): ObbPaths

	@Binds
	fun bindSaveDataPaths(saveDataPaths: SaveDataPathsImpl): SaveDataPaths
}