package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.core.services.files.PatchableGameFile
import solru.okkeipatcher.core.services.files.SaveData

interface GameFileStrategy {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: SaveData
}