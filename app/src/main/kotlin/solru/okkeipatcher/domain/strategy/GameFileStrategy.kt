package solru.okkeipatcher.domain.strategy

import solru.okkeipatcher.domain.services.gamefile.Apk
import solru.okkeipatcher.domain.services.gamefile.PatchableGameFile
import solru.okkeipatcher.domain.services.gamefile.SaveData

interface GameFileStrategy : AutoCloseable {

	val apk: Apk
	val obb: PatchableGameFile
	val saveData: SaveData

	override fun close() {
		apk.close()
		saveData.close()
	}
}