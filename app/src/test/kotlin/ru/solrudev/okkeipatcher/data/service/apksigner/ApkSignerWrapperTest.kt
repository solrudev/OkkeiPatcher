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

package ru.solrudev.okkeipatcher.data.service.apksigner

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import ru.solrudev.okkeipatcher.data.FailingFileSystem
import ru.solrudev.okkeipatcher.data.repository.FakeHashRepository
import ru.solrudev.okkeipatcher.data.util.read
import ru.solrudev.okkeipatcher.data.util.write
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApkSignerWrapperTest {

	private val inputApkPath = "/file.apk".toPath()
	private val inputApkContent = "ApkSignerWrapper input test string"
	private val signedApkContent = "ApkSignerWrapper output test string"
	private val expectedSignedApkHash = "b83021ee45ae504358eb1e3f1550c5dd3b54b199008ce2131123bca6f7827a50"
	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()
	private val failingFileSystem = FailingFileSystem(fileSystem)

	private val apkSignerImplementation = object : ApkSignerImplementation {
		override suspend fun sign(apk: Path, outputApk: Path) {
			fileSystem.write(outputApk, signedApkContent)
		}
	}

	@BeforeTest
	fun setUp() {
		fileSystem.write(inputApkPath, inputApkContent)
	}

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `WHEN apk is signed THEN apk contains signed apk content`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, fileSystem, apkSignerImplementation
		)

		// WHEN
		apkSigner.sign(inputApkPath)
		val actualApkContent = fileSystem.read(inputApkPath)

		// THEN
		assertEquals(signedApkContent, actualApkContent)
	}

	@Test
	fun `WHEN apk is signed THEN signedApkHash in hash repository contains signed apk hash`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, fileSystem, apkSignerImplementation
		)

		// WHEN
		apkSigner.sign(inputApkPath)
		val actualSignedApkHash = hashRepository.signedApkHash.retrieve()

		// THEN
		assertEquals(expectedSignedApkHash, actualSignedApkHash)
	}

	@Test
	fun `WHEN apk is signed and exception is thrown THEN apk remains unchanged`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem, apkSignerImplementation
		)

		// WHEN
		try {
			apkSigner.sign(inputApkPath)
		} catch (_: Throwable) {
		}
		val actualInputApkContent = fileSystem.read(inputApkPath)

		// THEN
		assertEquals(inputApkContent, actualInputApkContent)
	}
}