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

package ru.solrudev.okkeipatcher.ui.main.util

import android.widget.TextView
import androidx.annotation.StringRes
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.isProgressActive
import com.github.razir.progressbutton.showProgress

/**
 * Set visibility of circular loading progress indicator displaying instead of text.
 * @param value Progress indicator visibility.
 * @param text Button text.
 */
fun TextView.setLoading(value: Boolean, @StringRes text: Int) {
	if (value) {
		showProgress { progressColor = currentTextColor }
	} else if (isProgressActive()) {
		hideProgress(text)
	}
}