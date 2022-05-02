package ru.solrudev.okkeipatcher.domain.file

import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class SignedApk(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.external.absolutePath, "signed.apk"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.signed_apk_hash.name)
}