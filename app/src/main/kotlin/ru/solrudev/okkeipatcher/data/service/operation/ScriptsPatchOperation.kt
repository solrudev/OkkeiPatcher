package ru.solrudev.okkeipatcher.data.service.operation

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.service.util.use
import ru.solrudev.okkeipatcher.data.util.download
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ScriptsCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import java.io.File

private val tempZipFilesRegex = Regex("(apk|zip)\\d+")

class ScriptsPatchOperation(
	private val apkZipPackageFactory: ApkZipPackageFactory,
	private val scriptsPatchFile: PatchFile,
	private val ioDispatcher: CoroutineDispatcher,
	private val applicationContext: Context,
	private val fileDownloader: FileDownloader
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
	private val scriptsFile = File(applicationContext.externalDir, "scripts.zip")
	private val extractedScriptsDirectory = File(applicationContext.externalDir, "script")

	override suspend fun invoke() = try {
		operation()
	} finally {
		deleteTempZipFiles()
		if (extractedScriptsDirectory.exists()) {
			extractedScriptsDirectory.deleteRecursively()
		}
		scriptsFile.delete()
	}

	private fun downloadScripts() = operation(progressMax = fileDownloader.progressMax) {
		status(LocalizedString.resource(R.string.status_downloading_scripts))
		val scriptsData = scriptsPatchFile.getData()
		val scriptsHash = fileDownloader.download(scriptsData.url, scriptsFile, hashing = true, ::progressDelta)
		if (scriptsHash != scriptsData.hash) {
			throw ScriptsCorruptedException()
		}
		scriptsPatchFile.installedVersion.persist(scriptsData.version)
	}

	private fun extractScripts() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_extracting_scripts))
		ZipFile(scriptsFile).use {
			withContext(ioDispatcher) {
				it.extractAll(extractedScriptsDirectory.absolutePath)
			}
		}
	}

	private fun replaceScripts() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_replacing_scripts))
		val scriptsFolder = "assets/script/"
		val newScripts = extractedScriptsDirectory
			.listFiles()!!
			.filter { it.isFile }
		val oldScripts = newScripts.map { "$scriptsFolder${it.name}" }
		apkZipPackageFactory.create().use { apk ->
			apk.removeFiles(oldScripts)
			apk.addFiles(newScripts, root = scriptsFolder)
			status(LocalizedString.resource(R.string.status_signing_apk))
			apk.removeSignature()
			apk.sign()
		}
	}

	private fun deleteTempZipFiles() {
		applicationContext.externalDir.listFiles()
			?.filter { it.extension.matches(tempZipFilesRegex) }
			?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
	}
}