package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.services.files.impl.SaveDataGeneric
import solru.okkeipatcher.core.services.files.impl.english.ApkEnglish
import solru.okkeipatcher.core.services.files.impl.english.ObbEnglish
import solru.okkeipatcher.core.strategy.GameFileStrategy
import javax.inject.Inject

class GameFileStrategyEnglish @Inject constructor(
	override val apk: ApkEnglish,
	override val obb: ObbEnglish,
	override val saveData: SaveDataGeneric
) : GameFileStrategy