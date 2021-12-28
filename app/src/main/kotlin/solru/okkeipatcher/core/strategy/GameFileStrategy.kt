package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.core.services.gamefiles.PatchableGameFile
import solru.okkeipatcher.core.services.gamefiles.SaveData

interface GameFileStrategy {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: SaveData
}