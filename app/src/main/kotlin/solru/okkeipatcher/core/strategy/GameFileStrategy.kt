package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.core.services.gamefile.PatchableGameFile
import solru.okkeipatcher.core.services.gamefile.SaveData

interface GameFileStrategy {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: SaveData
}