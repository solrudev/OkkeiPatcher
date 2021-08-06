package solru.okkeipatcher.core.impl.english

import solru.okkeipatcher.core.base.GameFileStrategy
import solru.okkeipatcher.core.files.impl.SaveDataGeneric
import solru.okkeipatcher.core.files.impl.english.ApkEnglish
import solru.okkeipatcher.core.files.impl.english.ObbEnglish
import javax.inject.Inject

class GameFileStrategyEnglish @Inject constructor(
	override val apk: ApkEnglish,
	override val obb: ObbEnglish,
	override val saveData: SaveDataGeneric
) : GameFileStrategy