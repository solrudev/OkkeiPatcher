package solru.okkeipatcher.domain.util.extension

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.domain.model.ProgressData

suspend inline fun MutableSharedFlow<ProgressData>.emit(currentProgress: Int, progressMax: Int) {
	emit(ProgressData(currentProgress, progressMax))
}

suspend inline fun MutableSharedFlow<ProgressData>.reset() {
	emit(ProgressData())
}

suspend inline fun MutableSharedFlow<ProgressData>.makeIndeterminate() {
	delay(15)
	emit(ProgressData(isIndeterminate = true))
}