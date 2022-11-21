package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toPath
import ru.solrudev.okkeipatcher.data.util.getPackageInfoCompat
import javax.inject.Inject

interface OkkeiPatcherApkProvider {
	fun getOkkeiPatcherApkPath(): Path
}

class OkkeiPatcherApkProviderImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : OkkeiPatcherApkProvider {

	override fun getOkkeiPatcherApkPath(): Path {
		return applicationContext.packageManager
			.getPackageInfoCompat(applicationContext.packageName, 0)
			.applicationInfo
			.publicSourceDir
			.toPath()
	}
}