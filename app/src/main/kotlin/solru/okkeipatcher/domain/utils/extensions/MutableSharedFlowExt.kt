package solru.okkeipatcher.domain.utils.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.data.ProgressData

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