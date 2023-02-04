package ru.solrudev.okkeipatcher.domain.gamefile.game

import ru.solrudev.okkeipatcher.domain.gamefile.SaveData
import ru.solrudev.okkeipatcher.domain.gamefile.english.DefaultApk
import ru.solrudev.okkeipatcher.domain.gamefile.english.DefaultObb
import javax.inject.Inject

class DefaultGame @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveData
) : Game