package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.services.gamefile.impl.SaveDataImpl
import solru.okkeipatcher.core.services.gamefile.impl.english.EnglishApk
import solru.okkeipatcher.core.services.gamefile.impl.english.EnglishObb
import solru.okkeipatcher.core.strategy.GameFileStrategy
import javax.inject.Inject

class EnglishGameFileStrategy @Inject constructor(
	override val apk: EnglishApk,
	override val obb: EnglishObb,
	override val saveData: SaveDataImpl
) : GameFileStrategy