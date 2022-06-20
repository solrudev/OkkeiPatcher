package ru.solrudev.okkeipatcher.domain.service.operation

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.externalDir
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.english.PatchFileVersionKey
import ru.solrudev.okkeipatcher.domain.util.extension.use
import ru.solrudev.okkeipatcher.io.service.HttpDownloader
import ru.solrudev.okkeipatcher.io.util.extension.recreate
import ru.solrudev.okkeipatcher.util.Preferences
import java.io.File

private val tempZipFilesRegex = Regex("(apk|zip)\\d+")

class ScriptsPatchOperation @AssistedInject constructor(
	@Assisted private val apk: Apk,
	@Assisted private val scriptsDataRepository: ScriptsDataRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context,
	private val httpDownloader: HttpDownloader
) : Operation<Unit> {

	private val operation = aggregateOperation(
		downloadScripts(),
		extractScripts(),
		replaceScripts(),
		apk.removeSignature(),
		apk.sign()
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

	private fun downloadScripts() = operation(progressMax = httpDownloader.progressMax) {
		status(LocalizedString.resource(R.string.status_downloading_scripts))
		val scriptsData = scriptsDataRepository.getScriptsData()
		scriptsFile.recreate()
		val outputStream = scriptsFile.outputStream()
		val scriptsHash = httpDownloader.download(scriptsData.url, outputStream, hashing = true) { progressDelta ->
			progressDelta(progressDelta)
		}
		if (scriptsHash != scriptsData.hash) {
			throw LocalizedException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
		}
		Preferences.set(PatchFileVersionKey.scripts_version.name, scriptsData.version)
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
		apk.toZipFile().use { apkZip ->
			val scriptsFolder = "assets/script/"
			val parameters = ZipParameters().apply { rootFolderNameInZip = scriptsFolder }
			val newScripts = extractedScriptsDirectory.listFiles()!!.filter { it.isFile }
			val oldScripts = newScripts.map { "$scriptsFolder${it.name}" }
			withContext(ioDispatcher) {
				apkZip.removeFiles(oldScripts)
				apkZip.addFiles(newScripts, parameters)
			}
		}
	}

	private fun deleteTempZipFiles() {
		applicationContext.externalDir.listFiles()
			?.filter { it.extension.matches(tempZipFilesRegex) }
			?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
	}
}