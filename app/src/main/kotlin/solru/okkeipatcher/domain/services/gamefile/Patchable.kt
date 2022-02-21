package solru.okkeipatcher.domain.services.gamefile

import solru.okkeipatcher.data.LocalizedString

interface Patchable {
	fun canPatch(onNegative: (message: LocalizedString) -> Unit = {}): Boolean
	suspend fun patch()
	suspend fun update()
}