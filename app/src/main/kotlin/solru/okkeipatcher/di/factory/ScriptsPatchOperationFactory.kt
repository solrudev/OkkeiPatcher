package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.domain.gamefile.AbstractApk
import solru.okkeipatcher.domain.operation.ScriptsPatchOperation
import solru.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import solru.okkeipatcher.io.file.VerifiableFile

@AssistedFactory
interface ScriptsPatchOperationFactory {

	fun create(
		apk: AbstractApk,
		scriptsDataRepository: ScriptsDataRepository,
		scriptsFile: VerifiableFile
	): ScriptsPatchOperation
}