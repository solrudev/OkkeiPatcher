package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy

import ru.solrudev.okkeipatcher.domain.service.gamefile.SaveData
import ru.solrudev.okkeipatcher.domain.service.gamefile.english.DefaultApk
import ru.solrudev.okkeipatcher.domain.service.gamefile.english.DefaultObb
import javax.inject.Inject

class DefaultGameFileStrategy @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveData
) : GameFileStrategy