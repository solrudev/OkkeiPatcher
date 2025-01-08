/*
 * Okkei Patcher
 * Copyright (C) 2023-2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.domain.operation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.model.PatchFileType
import ru.solrudev.okkeipatcher.domain.model.exception.ScriptsCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.util.prepareRecreate

class ApkPatchOperation(
	private val apkPatchFiles: PatchFiles,
	private val apkRepository: ApkRepository,
	private val signedApkHash: Persistable<String>,
	private val ioDispatcher: CoroutineDispatcher,
	externalDir: Path,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : Operation<Unit> {

	private val operation = aggregateOperation(
		downloadScripts(),
		extractScripts(),
		replaceScripts()
	)

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax
	private val scriptsFile = externalDir / "scripts.zip"
	private val extractedScriptsDirectory = externalDir / "script"

	override suspend fun skip() = operation.skip()

	override suspend fun invoke() = try {
		operation()
	} finally {
		if (fileSystem.exists(extractedScriptsDirectory)) {
			fileSystem.deleteRecursively(extractedScriptsDirectory)
		}
		fileSystem.delete(scriptsFile)
	}

	private fun downloadScripts(): Operation<Unit> = operation(progressMax = fileDownloader.progressMax) {
		status(R.string.status_downloading_scripts)
		val apkPatchData = apkPatchFiles
			.getData()
			.single { it.type == PatchFileType.SCRIPTS }
		val scriptsHash = fileDownloader.download(
			apkPatchData.url, scriptsFile, hashing = true, onProgressChanged = ::progressDelta
		)
		if (scriptsHash != apkPatchData.hash) {
			throw ScriptsCorruptedException()
		}
	}

	private fun extractScripts(): Operation<Unit> = operation(progressMax = 50) {
		status(R.string.status_extracting_scripts)
		runInterruptible(ioDispatcher) {
			val scriptsZip = fileSystem.openZip(scriptsFile)
			scriptsZip.list("/".toPath()).forEach { script ->
				val extractedScript = extractedScriptsDirectory / script.name
				scriptsZip.source(script).use { source ->
					fileSystem.prepareRecreate(extractedScript)
					fileSystem.sink(extractedScript).buffer().use { sink ->
						sink.writeAll(source)
					}
				}
			}
		}
	}

	private fun replaceScripts(): Operation<Unit> = operation(progressMax = 50) {
		status(R.string.status_replacing_scripts)
		val scriptsFolder = "assets/script/"
		val newScripts = fileSystem.list(extractedScriptsDirectory)
		val oldScripts = newScripts.map { "$scriptsFolder${it.name}" }
		apkRepository.createTemp().use { apk ->
			apk.removeFiles(oldScripts)
			apk.addFiles(newScripts, root = scriptsFolder)
			status(R.string.status_signing_apk)
			apk.removeSignature()
			apk.sign()
			val hash = apk.computeHash()
			signedApkHash.persist(hash)
		}
	}
}