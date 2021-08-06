package solru.okkeipatcher.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdatesEnglish

val JsonSerializer = Json {
	ignoreUnknownKeys = true
	serializersModule = SerializersModule {
		polymorphic(PatchUpdates::class) {
			subclass(PatchUpdatesEnglish::class)
		}
	}
}