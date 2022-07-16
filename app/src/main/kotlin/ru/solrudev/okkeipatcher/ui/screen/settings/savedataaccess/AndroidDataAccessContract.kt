package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_URI

class AndroidDataAccessContract(private val applicationContext: Context) : ActivityResultContract<Unit, Boolean>() {

	override fun createIntent(context: Context, input: Unit) = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
		.putExtra(DocumentsContract.EXTRA_INITIAL_URI, ANDROID_DATA_URI)

	override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
		if (resultCode != Activity.RESULT_OK) {
			return false
		}
		val directoryUri = intent?.data ?: return false
		applicationContext
			.contentResolver
			.takePersistableUriPermission(
				directoryUri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
			)
		return isSaveDataAccessGranted()
	}

	private fun isSaveDataAccessGranted() = applicationContext
		.contentResolver
		.persistedUriPermissions
		.any { it.uri == ANDROID_DATA_TREE_URI && it.isReadPermission && it.isWritePermission }
}