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

package ru.solrudev.okkeipatcher.data.repository.gamefile

import okio.fakefilesystem.FakeFileSystem
import ru.solrudev.okkeipatcher.data.FakePatcherEnvironment
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.write
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObbRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val fileSystem = FakeFileSystem()

	private val obb =
		environment.externalStoragePath / "Android/obb/$GAME_PACKAGE_NAME/main.87.com.mages.chaoschild_jp.obb"

	@AfterTest
	fun tearDown() {
		fileSystem.checkNoOpenFiles()
	}

	@Test
	fun `obbExists returns obb existence`() {
		val obbRepository = ObbRepositoryImpl(environment, fileSystem)
		fileSystem.write(obb, "some content")
		val exists = obbRepository.obbExists
		fileSystem.delete(obb)
		val notExists = obbRepository.obbExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN obb is deleted THEN it doesn't exist`() {
		val obbRepository = ObbRepositoryImpl(environment, fileSystem)
		fileSystem.write(obb, "some content")
		obbRepository.deleteObb()
		assertFalse(fileSystem.exists(obb))
	}
}