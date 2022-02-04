package solru.okkeipatcher.domain.services.gamefile

interface Patchable {
	suspend fun patch()
	suspend fun update()
}