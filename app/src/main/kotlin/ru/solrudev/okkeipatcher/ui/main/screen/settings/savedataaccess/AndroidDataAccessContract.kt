/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_URI

@RequiresApi(Build.VERSION_CODES.O)
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