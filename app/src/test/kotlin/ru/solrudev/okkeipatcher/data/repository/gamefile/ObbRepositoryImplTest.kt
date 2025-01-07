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
import ru.solrudev.okkeipatcher.data.util.write
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObbRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val obb = environment.obbPath
	private val patchedObb = environment.filesPath / "patched.obb"
	private val expectedContent = "patched content"
	private val fileSystem = FakeFileSystem()
	private val obbRepository = ObbRepositoryImpl(environment, fileSystem)

	@BeforeTest
	fun setUp() {
		fileSystem.write(patchedObb, expectedContent)
		fileSystem.delete(obb)
	}

	@AfterTest
	fun tearDown() {
		fileSystem.checkNoOpenFiles()
	}

	@Test
	fun `obbExists returns obb existence`() {
		fileSystem.write(obb, "some content")
		val exists = obbRepository.obbExists
		fileSystem.delete(obb)
		val notExists = obbRepository.obbExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN obb is deleted THEN it doesn't exist`() {
		fileSystem.write(obb, "some content")
		obbRepository.deleteObb()
		assertFalse(fileSystem.exists(obb))
	}
}