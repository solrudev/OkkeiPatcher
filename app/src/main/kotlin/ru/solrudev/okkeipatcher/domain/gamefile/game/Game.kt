package ru.solrudev.okkeipatcher.domain.gamefile.game

import ru.solrudev.okkeipatcher.domain.gamefile.GameFile
import ru.solrudev.okkeipatcher.domain.gamefile.PatchableGameFile

interface Game : PatchableGame, BackupableGame {

	override fun close() {
		apk.close()
		obb.close()
		saveData.close()
	}
}

interface PatchableGame : AutoCloseable {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: GameFile
}

interface BackupableGame : AutoCloseable {
	val apk: GameFile
	val obb: GameFile
	val saveData: GameFile
}