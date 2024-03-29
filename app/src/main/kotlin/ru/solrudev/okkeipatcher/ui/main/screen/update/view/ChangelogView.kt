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

package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.widget.TextView
import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.isChangelogVisible
import ru.solrudev.okkeipatcher.ui.widget.CollapsingCardView

class ChangelogView(
	private val changelogTextView: TextView,
	private val changelogCard: CollapsingCardView
) : JetView<UpdateUiState> {

	override val trackedState = listOf(UpdateUiState::changelog, UpdateUiState::isChangelogVisible)

	override fun render(uiState: UpdateUiState) {
		changelogCard.isVisible = uiState.isChangelogVisible
		changelogTextView.text = uiState.changelog.flatten()
	}

	private fun Map<String, List<String>>.flatten(): CharSequence {
		return entries.joinTo(buffer = SpannableStringBuilder(), separator = "\n\n") { (version, changes) ->
			SpannableStringBuilder().append("$version\n").append(changes.toBulletedList())
		}
	}

	private fun List<String>.toBulletedList(): CharSequence {
		val spannableString = SpannableString(this.joinToString("\n"))
		foldIndexed(0) { index, acc, span ->
			val end = acc + span.length + if (index != size - 1) 1 else 0
			spannableString.setSpan(BulletSpan(16), acc, end, 0)
			return@foldIndexed end
		}
		return spannableString
	}
}