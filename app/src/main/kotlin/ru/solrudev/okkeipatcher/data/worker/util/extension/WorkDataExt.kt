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

package ru.solrudev.okkeipatcher.data.worker.util.extension

import androidx.work.Data
import java.io.*

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Data.getSerializable(key: String): T? {
	val byteArray = getByteArray(key) ?: return null
	ByteArrayInputStream(byteArray).use { byteArrayInputStream ->
		ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
			return objectInputStream.readObject() as T
		}
	}
}

fun Data.Builder.putSerializable(key: String, value: Serializable) =
	ByteArrayOutputStream().use { byteArrayOutputStream ->
		ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
			objectOutputStream.writeObject(value)
			objectOutputStream.flush()
		}
		putByteArray(key, byteArrayOutputStream.toByteArray())
	}