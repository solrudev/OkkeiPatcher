package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.domain.model.LocalizedString

interface Patchable {
	fun canPatch(onNegative: (message: LocalizedString) -> Unit = {}): Boolean
	suspend fun patch()
	suspend fun update()
}