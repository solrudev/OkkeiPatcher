package ru.solrudev.okkeipatcher.data.operation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.ScriptsCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile

class ScriptsPatchOperation(
	private val scriptsPatchFile: PatchFile,
	private val apkRepository: ApkRepository,
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
		val scriptsData = scriptsPatchFile.getData()
		val scriptsHash = fileDownloader.download(
			scriptsData.url, scriptsFile, hashing = true, onProgressDeltaChanged = ::progressDelta
		)
		if (scriptsHash != scriptsData.hash) {
			throw ScriptsCorruptedException()
		}
		scriptsPatchFile.installedVersion.persist(scriptsData.version)
	}

	private fun extractScripts(): Operation<Unit> = operation(progressMax = 100) {
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

	private fun replaceScripts(): Operation<Unit> = operation(progressMax = 100) {
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
		}
	}
}