package solru.okkeipatcher.core.services.gamefile

interface Patchable {
	suspend fun patch()
	suspend fun update()
}