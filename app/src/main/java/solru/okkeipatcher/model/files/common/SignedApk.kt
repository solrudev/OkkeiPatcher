package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class SignedApk(ioService: IoService) :
	VerifiableFileWrapper(
		JavaFile(
			File(OkkeiStorage.external.absolutePath, "signed.apk"),
			ioService
		), ioService
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.signed_apk_hash.name)
}