package solru.okkeipatcher.core

import kotlinx.serialization.json.Json

val JsonSerializer = Json {
	ignoreUnknownKeys = true
}