package solru.okkeipatcher.io

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.base.BaseFile
import solru.okkeipatcher.io.base.File
import solru.okkeipatcher.io.base.Verifiable
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.empty
import solru.okkeipatcher.utils.extensions.isEmptyOrBlank

abstract class VerifiableFile(
	private val fileImplementation: BaseFile,
	private val progressProvider: ProgressProviderImpl
) : File by fileImplementation, Verifiable, ProgressProvider {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(fileImplementation.progress, progressProvider.mutableProgress)

	protected suspend fun compareBySharedPreferences(key: String): Boolean {
		var hash = String.empty
		val hashToCompare = Preferences.get(key, String.empty)
		if (hashToCompare.isEmptyOrBlank()) {
			return false
		}
		if (this.exists) {
			hash = fileImplementation.computeHash()
		}
		return hash == hashToCompare
	}

	protected suspend fun compareByFile(file: File): Boolean {
		var hash = String.empty
		var hashToCompare = String.empty
		if (file.exists) {
			coroutineScope {
				val progressJob = launch {
					progressProvider.mutableProgress.emitAll(file.progress)
				}
				hashToCompare = file.computeHash()
				progressJob.cancel()
			}
		}
		if (hashToCompare.isEmptyOrBlank()) {
			return false
		}
		if (this.exists) {
			hash = this.computeHash()
		}
		return hash == hashToCompare
	}
}