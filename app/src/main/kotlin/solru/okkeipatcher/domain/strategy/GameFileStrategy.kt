package solru.okkeipatcher.domain.strategy

import solru.okkeipatcher.domain.services.gamefile.PatchableGameFile
import solru.okkeipatcher.domain.services.gamefile.SaveData

interface GameFileStrategy {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: SaveData
}