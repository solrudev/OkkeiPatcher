package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class SignedApk(ioService: IoService) :
	VerifiableFile(
		JavaFile(
			File(OkkeiStorage.external.absolutePath, "signed.apk"),
			ioService
		), ProgressProviderImpl()
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.signed_apk_hash.name)
}