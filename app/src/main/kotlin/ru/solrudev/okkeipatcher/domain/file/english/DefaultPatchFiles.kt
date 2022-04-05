package ru.solrudev.okkeipatcher.domain.file.english

import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import javax.inject.Inject

class DefaultPatchFiles @Inject constructor(streamCopier: StreamCopier) {
	val scripts: VerifiableFile by lazy { Scripts(streamCopier) }
}