package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.services.gamefile.impl.SaveDataImpl
import solru.okkeipatcher.core.services.gamefile.impl.english.DefaultApk
import solru.okkeipatcher.core.services.gamefile.impl.english.DefaultObb
import solru.okkeipatcher.core.strategy.GameFileStrategy
import javax.inject.Inject

class DefaultGameFileStrategy @Inject constructor(
	override val apk: DefaultApk,
	override val obb: DefaultObb,
	override val saveData: SaveDataImpl
) : GameFileStrategy