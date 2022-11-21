package ru.solrudev.okkeipatcher.data.repository.gamefile.util

import okio.Path
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment

val OkkeiEnvironment.backupPath: Path
	get() = externalFilesPath / "backup"