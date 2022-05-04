package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.english

import ru.solrudev.okkeipatcher.domain.service.gamefile.SaveData
import ru.solrudev.okkeipatcher.domain.service.gamefile.english.DefaultApk
import ru.solrudev.okkeipatcher.domain.service.gamefile.english.DefaultObb
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import javax.inject.Inject

class DefaultGameFileStrategy @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveData
) : GameFileStrategy