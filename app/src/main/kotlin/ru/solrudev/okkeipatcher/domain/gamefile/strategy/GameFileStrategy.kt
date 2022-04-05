package ru.solrudev.okkeipatcher.domain.gamefile.strategy

import ru.solrudev.okkeipatcher.domain.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.gamefile.PatchableGameFile
import ru.solrudev.okkeipatcher.domain.gamefile.SaveData

interface GameFileStrategy : AutoCloseable {

	val apk: Apk
	val obb: PatchableGameFile
	val saveData: SaveData

	override fun close() {
		apk.close()
		saveData.close()
	}
}