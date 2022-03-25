package solru.okkeipatcher.domain.gamefile.strategy

import solru.okkeipatcher.domain.gamefile.Apk
import solru.okkeipatcher.domain.gamefile.PatchableGameFile
import solru.okkeipatcher.domain.gamefile.SaveData

interface GameFileStrategy : AutoCloseable {

	val apk: Apk
	val obb: PatchableGameFile
	val saveData: SaveData

	override fun close() {
		apk.close()
		saveData.close()
	}
}