package solru.okkeipatcher.io.exceptions

import io.ktor.http.*
import java.io.IOException

class HttpStatusCodeException(val statusCode: HttpStatusCode) : IOException() {
	override val message = "${statusCode.value} ${statusCode.description}"
}