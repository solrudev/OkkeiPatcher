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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.ForwardingFileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import ru.solrudev.okkeipatcher.data.repository.FakeHashRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ApkSignerWrapperTest {

	private val inputApkPath = "/file.apk".toPath()
	private val inputApkContent = "ApkSignerWrapper input test string"
	private val signedApkContent = "ApkSignerWrapper output test string"
	private val expectedSignedApkHash = "b83021ee45ae504358eb1e3f1550c5dd3b54b199008ce2131123bca6f7827a50"

	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()

	private val failingFileSystem = object : ForwardingFileSystem(fileSystem) {
		override fun atomicMove(source: Path, target: Path) {
			throw IOException("synthetic failure")
		}
	}

	private val apkSignerImplementation = object : ApkSignerImplementation {
		override suspend fun sign(apk: Path, outputApk: Path) {
			outputApk.write(signedApkContent)
		}
	}

	@BeforeTest
	fun setup() {
		inputApkPath.write(inputApkContent)
	}

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `input apk must contain signed apk content after signing`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, fileSystem, apkSignerImplementation
		)
		apkSigner.sign(inputApkPath)
		val actualInputApkContent = inputApkPath.read()
		assertEquals(signedApkContent, actualInputApkContent)
	}

	@Test
	fun `signed apk hash must be written to signedApkHash dao of hash repository`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, fileSystem, apkSignerImplementation
		)
		apkSigner.sign(inputApkPath)
		val actualSignedApkHash = hashRepository.signedApkHash.retrieve()
		assertEquals(expectedSignedApkHash, actualSignedApkHash)
	}

	@Test
	fun `if exception is thrown, input apk must be unchanged`() = runTest {
		val apkSigner = ApkSignerWrapper(
			StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem, apkSignerImplementation
		)
		try {
			apkSigner.sign(inputApkPath)
		} catch (_: Throwable) {
		}
		val actualInputApkContent = inputApkPath.read()
		assertEquals(inputApkContent, actualInputApkContent)
	}

	private fun Path.read(): String {
		return fileSystem.source(this).buffer().use { it.readUtf8() }
	}

	private fun Path.write(content: String) {
		fileSystem.sink(this).buffer().use { it.writeUtf8(content) }
	}
}