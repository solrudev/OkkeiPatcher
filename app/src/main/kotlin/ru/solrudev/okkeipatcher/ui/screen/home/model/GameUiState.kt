package ru.solrudev.okkeipatcher.ui.screen.home.model

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
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
		ContextCompat.getColor(context, R.color.color_game_icon_placeholder)
	)
	return GameUiState(title, version, icon)
}