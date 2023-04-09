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

package ru.solrudev.okkeipatcher.ui.main.screen.home.model

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.getPackageInfoCompat

data class GameUiState(
	val title: CharSequence,
	val version: CharSequence,
	val icon: Drawable
)

fun GameUiState(context: Context): GameUiState {
	val packageInfo = try {
		context.packageManager.getPackageInfoCompat(GAME_PACKAGE_NAME, PackageManager.GET_META_DATA)
	} catch (_: PackageManager.NameNotFoundException) {
		null
	}
	val title = packageInfo
		?.applicationInfo
		?.loadLabel(context.packageManager) ?: context.getString(R.string.game_title)
	val version = packageInfo?.versionName ?: context.getString(R.string.not_available)
	val icon = packageInfo?.applicationInfo?.loadIcon(context.packageManager) ?: ColorDrawable(
		ContextCompat.getColor(context, R.color.color_app_icon_placeholder)
	)
	return GameUiState(title, version, icon)
}