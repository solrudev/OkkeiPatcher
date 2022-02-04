package solru.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import solru.okkeipatcher.core.services.ScriptsPatcher
import solru.okkeipatcher.core.services.gamefile.impl.Apk
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.repository.patch.ScriptsDataRepository

@AssistedFactory
interface ScriptsPatcherFactory {

	fun create(
		apk: Apk,
		scriptsDataRepository: ScriptsDataRepository,
		scriptsFile: VerifiableFile
	): ScriptsPatcher
}