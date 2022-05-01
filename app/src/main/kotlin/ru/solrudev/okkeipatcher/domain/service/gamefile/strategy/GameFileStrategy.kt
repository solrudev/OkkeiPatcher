package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy

import ru.solrudev.okkeipatcher.domain.service.gamefile.GameFile
import ru.solrudev.okkeipatcher.domain.service.gamefile.PatchableGameFile

interface GameFileStrategy : PatchStrategy, RestoreStrategy {

	override fun close() {
		apk.close()
		obb.close()
		saveData.close()
	}
}

interface PatchStrategy : AutoCloseable {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: GameFile
}

interface RestoreStrategy : AutoCloseable {
	val apk: GameFile
	val obb: GameFile
	val saveData: GameFile
}