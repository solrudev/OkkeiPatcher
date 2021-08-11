package solru.okkeipatcher.pm

import android.content.pm.PackageManager
import android.os.Build
import solru.okkeipatcher.MainApplication

object PackageManager {

	@Suppress("DEPRECATION")
	val versionCode: Int
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) MainApplication.context.packageManager.getPackageInfo(
			MainApplication.context.packageName,
			0
		).longVersionCode.toInt() else MainApplication.context.packageManager.getPackageInfo(
			MainApplication.context.packageName,
			0
		).versionCode

	val versionString: String
		get() {
			val pm = MainApplication.context.packageManager
			val packageName = MainApplication.context.packageName
			val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
			return info.versionName
		}

	val packageName: String get() = MainApplication.context.packageName

	fun isPackageInstalled(packageName: String?): Boolean {
		if (packageName == null) return false
		return try {
			MainApplication.context.packageManager.getPackageInfo(
				packageName,
				PackageManager.GET_ACTIVITIES
			)
			true
		} catch (e: PackageManager.NameNotFoundException) {
			false
		}
	}

	fun getPackagePublicSourceDir(packageName: String): String =
		MainApplication.context.packageManager
			.getPackageInfo(packageName, 0)
			.applicationInfo
			.publicSourceDir
}