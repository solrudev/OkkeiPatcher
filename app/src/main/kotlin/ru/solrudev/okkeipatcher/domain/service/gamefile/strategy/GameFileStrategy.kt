package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy

import ru.solrudev.okkeipatcher.domain.service.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.service.gamefile.PatchableGameFile
import ru.solrudev.okkeipatcher.domain.service.gamefile.SaveData

interface GameFileStrategy : AutoCloseable {

	val apk: Apk
	val obb: PatchableGameFile
	val saveData: SaveData

	override fun close() {
		apk.close()
		saveData.close()
	}
}