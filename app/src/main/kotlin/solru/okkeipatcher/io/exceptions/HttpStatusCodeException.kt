package solru.okkeipatcher.io.exceptions

import io.ktor.http.*

class HttpStatusCodeException(val statusCode: HttpStatusCode) :
	Exception("${statusCode.value} ${statusCode.description}")