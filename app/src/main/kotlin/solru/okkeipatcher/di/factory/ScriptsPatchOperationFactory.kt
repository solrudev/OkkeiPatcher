package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.domain.gamefile.impl.AbstractApk
import solru.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import solru.okkeipatcher.domain.service.ScriptsPatchOperation
import solru.okkeipatcher.io.file.VerifiableFile

@AssistedFactory
interface ScriptsPatchOperationFactory {

	fun create(
		apk: AbstractApk,
		scriptsDataRepository: ScriptsDataRepository,
		scriptsFile: VerifiableFile
	): ScriptsPatchOperation
}