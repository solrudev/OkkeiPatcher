package ru.solrudev.okkeipatcher.data

import android.content.Context
import android.os.Environment
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.data.util.versionCode
import java.util.*
import javax.inject.Inject

interface OkkeiEnvironment {
	val locale: Locale
	val versionCode: Int
	val filesPath: Path
	val externalFilesPath: Path
	val externalStoragePath: Path
}

class OkkeiEnvironmentImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : OkkeiEnvironment {

	override val locale: Locale
		get() = ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0] ?: Locale.ENGLISH

	override val versionCode: Int
		get() = applicationContext.versionCode

	override val filesPath: Path
		get() = applicationContext.filesDir.toOkioPath()

	override val externalFilesPath: Path
		get() = applicationContext.externalDir.toOkioPath()

	override val externalStoragePath: Path
		get() = Environment.getExternalStorageDirectory().toOkioPath()
}