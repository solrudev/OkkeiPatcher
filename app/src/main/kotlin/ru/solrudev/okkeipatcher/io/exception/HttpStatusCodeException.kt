package ru.solrudev.okkeipatcher.io.exception

import io.ktor.http.*
import java.io.IOException

class HttpStatusCodeException(statusCode: HttpStatusCode) : IOException() {
	override val message = "${statusCode.value} ${statusCode.description}"
}