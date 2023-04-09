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

package ru.solrudev.okkeipatcher.data.service.util

import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.buffer
import okio.source
import ru.solrudev.okkeipatcher.data.util.copyTo
import java.io.File

inline fun File.computeHash(onProgressDeltaChanged: (Int) -> Unit = {}): String {
	source().buffer().use { source ->
		val hashingSink = sha256(blackholeSink())
		hashingSink.buffer().use { sink ->
			source.copyTo(sink, length(), onProgressDeltaChanged)
		}
		return hashingSink.hash.hex()
	}
}