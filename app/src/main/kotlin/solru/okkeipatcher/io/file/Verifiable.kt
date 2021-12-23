package solru.okkeipatcher.io.file

interface Verifiable {
	suspend fun verify(): Boolean
}