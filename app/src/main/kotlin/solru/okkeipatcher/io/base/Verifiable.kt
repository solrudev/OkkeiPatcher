package solru.okkeipatcher.io.base

interface Verifiable {
	suspend fun verify(): Boolean
}