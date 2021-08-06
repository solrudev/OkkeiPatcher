package solru.okkeipatcher.pm.activityresult

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.pm.PackageManager

@RequiresApi(Build.VERSION_CODES.O)
class InstallPermissionContract : ActivityResultContract<Unit, Boolean>() {

	override fun createIntent(context: Context, input: Unit?) = Intent(
		android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
		Uri.parse("package:${PackageManager.packageName}")
	)

	override fun parseResult(resultCode: Int, intent: Intent?) =
		MainApplication.context.packageManager.canRequestPackageInstalls()

	override fun getSynchronousResult(context: Context, input: Unit?) =
		SynchronousResult(MainApplication.context.packageManager.canRequestPackageInstalls())
}