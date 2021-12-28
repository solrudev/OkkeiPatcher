package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.services.gamefiles.impl.SaveDataImpl
import solru.okkeipatcher.core.services.gamefiles.impl.english.ApkEnglish
import solru.okkeipatcher.core.services.gamefiles.impl.english.ObbEnglish
import solru.okkeipatcher.core.strategy.GameFileStrategy
import javax.inject.Inject

class GameFileStrategyEnglish @Inject constructor(
	override val apk: ApkEnglish,
	override val obb: ObbEnglish,
	override val saveData: SaveDataImpl
) : GameFileStrategy