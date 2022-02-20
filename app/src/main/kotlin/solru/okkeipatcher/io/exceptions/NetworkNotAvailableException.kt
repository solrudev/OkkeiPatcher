package solru.okkeipatcher.io.exceptions

import java.io.IOException

class NetworkNotAvailableException : IOException() {
	override val message = "Network is not available"
}