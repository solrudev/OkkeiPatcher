package ru.solrudev.okkeipatcher.domain.gamefile.strategy.impl.english

import ru.solrudev.okkeipatcher.domain.gamefile.SaveDataImpl
import ru.solrudev.okkeipatcher.domain.gamefile.english.DefaultApk
import ru.solrudev.okkeipatcher.domain.gamefile.english.DefaultObb
import ru.solrudev.okkeipatcher.domain.gamefile.strategy.GameFileStrategy
import javax.inject.Inject

class DefaultGameFileStrategy @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveDataImpl
) : GameFileStrategy