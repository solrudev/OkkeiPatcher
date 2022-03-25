package solru.okkeipatcher.io.file

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import solru.okkeipatcher.domain.base.ProgressPublisherImpl
import solru.okkeipatcher.util.Preferences
import solru.okkeipatcher.util.extension.empty
import solru.okkeipatcher.util.extension.isEmptyOrBlank

abstract class VerifiableFile(private val fileImplementation: File) : File by fileImplementation, Verifiable {

	private val progressPublisher = ProgressPublisherImpl()
	override val progress = merge(fileImplementation.progress, progressPublisher.progress)

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
					progressPublisher._progress.emitAll(file.progress)
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