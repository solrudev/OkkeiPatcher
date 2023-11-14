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

package ru.solrudev.okkeipatcher.data.service.factory

import okio.Path
import ru.solrudev.okkeipatcher.domain.service.ZipPackage

class MockZipPackageFactory : ApkZipPackageFactory {
	override fun create(tempPath: Path) = object : ZipPackage {
		override suspend fun addFiles(files: List<Path>, root: String) {}
		override suspend fun removeFiles(files: List<String>) {}
		override suspend fun sign() {}
		override suspend fun removeSignature() {}
		override fun close() {}
	}
}