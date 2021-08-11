package solru.okkeipatcher.core.base

import solru.okkeipatcher.core.files.base.PatchableGameFile
import solru.okkeipatcher.core.files.base.SaveData

interface GameFileStrategy {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: SaveData
}