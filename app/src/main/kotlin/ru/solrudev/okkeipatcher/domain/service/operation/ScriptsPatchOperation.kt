package ru.solrudev.okkeipatcher.domain.service.operation

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.module.IoDispatcher
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.file.english.PatchFileHashKey
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.AbstractApk
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.impl.english.PatchFileVersionKey
import ru.solrudev.okkeipatcher.domain.util.extension.use
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.HttpDownloader
import ru.solrudev.okkeipatcher.util.Preferences
import java.io.File

class ScriptsPatchOperation @AssistedInject constructor(
	@Assisted private val apk: AbstractApk,
	@Assisted private val scriptsDataRepository: ScriptsDataRepository,
	@Assisted private val scriptsFile: VerifiableFile,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val httpDownloader: HttpDownloader
) : Operation<Unit> {

	private val operation = AggregateOperation(
		listOf(
			downloadScripts(),
			extractScripts(),
			replaceScripts(),
			apk.removeSignature(),
			apk.sign()
		)
	)

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax
	private val extractedScriptsDirectory = File(OkkeiStorage.external, "script")

	override suspend fun invoke() = try {
		operation()
	} finally {
		extractedScriptsDirectory.run {
			if (exists()) deleteRecursively()
		}
		scriptsFile.delete()
	}

	private fun downloadScripts() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			status(LocalizedString.resource(R.string.status_downloading_scripts))
			val scriptsData = scriptsDataRepository.getScriptsData()
			scriptsFile.delete()
			scriptsFile.create()
			val outputStream = scriptsFile.createOutputStream()
			val scriptsHash = httpDownloader.download(scriptsData.url, outputStream, hashing = true) { progressDelta ->
				progressDelta(progressDelta)
			}
			status(LocalizedString.resource(R.string.status_comparing_scripts))
			if (scriptsHash != scriptsData.hash) {
				throw LocalizedException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
			}
			status(LocalizedString.resource(R.string.status_writing_scripts_hash))
			Preferences.set(PatchFileHashKey.scripts_hash.name, scriptsHash)
			Preferences.set(PatchFileVersionKey.scripts_version.name, scriptsData.version)
		}
	}

	private fun extractScripts() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			status(LocalizedString.resource(R.string.status_extracting_scripts))
			val scriptsZip = ZipFile(scriptsFile.fullPath)
			scriptsZip.use {
				withContext(ioDispatcher) {
					it.extractAll(extractedScriptsDirectory.absolutePath)
				}
				progressDelta(progressMax)
			}
		}
	}

	private fun replaceScripts() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		@Suppress("BlockingMethodInNonBlockingContext")
		override suspend fun invoke() = apk.asZipFile().use { apkZip ->
			status(LocalizedString.resource(R.string.status_replacing_scripts))
			val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
			val scriptsList = extractedScriptsDirectory.listFiles()!!.filter { it.isFile }
			val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
			withContext(ioDispatcher) {
				apkZip.removeFiles(apkScriptsList)
				apkZip.addFiles(scriptsList, parameters)
			}
			progressDelta(progressMax)
		}
	}
}