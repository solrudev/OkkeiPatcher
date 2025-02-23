/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.network.model

import com.squareup.moshi.JsonClass
import ru.solrudev.okkeipatcher.domain.model.PatchFileType

@JsonClass(generateAdapter = true)
data class PatchFileDto(
	val type: PatchFileType,
	val targetVersion: Int,
	val version: Int,
	val url: String,
	val hash: String,
	val size: Long,
	val patchedSize: Long,
	val compatibleHashes: List<String>
)