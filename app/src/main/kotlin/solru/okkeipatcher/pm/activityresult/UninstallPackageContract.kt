package solru.okkeipatcher.pm.activityresult

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import solru.okkeipatcher.pm.PackageManager

class UninstallPackageContract : ActivityResultContract<String, Boolean>() {

	private lateinit var packageName: String

	override fun createIntent(context: Context, input: String): Intent {
		packageName = input
		val packageUri = Uri.parse("package:$input")
		return Intent(Intent.ACTION_DELETE, packageUri)
	}

	override fun parseResult(resultCode: Int, intent: Intent?) =
		!PackageManager.isPackageInstalled(packageName)
}