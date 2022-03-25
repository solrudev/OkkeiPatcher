package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.util.isPackageInstalled

interface Apk : PatchableGameFile, AutoCloseable {

	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
		val isInstalled get() = isPackageInstalled(PACKAGE_NAME)
	}
}