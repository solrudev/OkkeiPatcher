package solru.okkeipatcher.exceptions.io

import io.ktor.http.*

class HttpStatusCodeException(val statusCode: HttpStatusCode) : Exception()