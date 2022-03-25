package solru.okkeipatcher.domain.gamefile.strategy.impl.english

import solru.okkeipatcher.domain.gamefile.impl.SaveDataImpl
import solru.okkeipatcher.domain.gamefile.impl.english.DefaultApk
import solru.okkeipatcher.domain.gamefile.impl.english.DefaultObb
import solru.okkeipatcher.domain.gamefile.strategy.GameFileStrategy
import javax.inject.Inject

class DefaultGameFileStrategy @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveDataImpl
) : GameFileStrategy