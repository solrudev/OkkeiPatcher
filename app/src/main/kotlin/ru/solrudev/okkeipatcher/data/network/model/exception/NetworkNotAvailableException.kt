package ru.solrudev.okkeipatcher.data.network.model.exception

import java.io.IOException

class NetworkNotAvailableException : IOException() {
	override val message = "Network is not available"
}