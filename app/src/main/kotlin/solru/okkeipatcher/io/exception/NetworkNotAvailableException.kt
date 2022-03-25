package solru.okkeipatcher.io.exception

import java.io.IOException

class NetworkNotAvailableException : IOException() {
	override val message = "Network is not available"
}