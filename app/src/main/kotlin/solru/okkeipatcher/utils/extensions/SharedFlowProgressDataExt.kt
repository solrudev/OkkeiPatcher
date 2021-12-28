package solru.okkeipatcher.utils.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.data.ProgressData

suspend inline fun MutableSharedFlow<ProgressData>.emit(currentProgress: Int, progressMax: Int) {
	emit(ProgressData(currentProgress, progressMax))
}

fun MutableSharedFlow<ProgressData>.tryEmit(currentProgress: Int, progressMax: Int) =
	tryEmit(ProgressData(currentProgress, progressMax))

suspend inline fun MutableSharedFlow<ProgressData>.reset() {
	emit(ProgressData())
}

fun MutableSharedFlow<ProgressData>.tryReset() = tryEmit(ProgressData())

suspend inline fun MutableSharedFlow<ProgressData>.makeIndeterminate() {
	delay(15)
	emit(ProgressData(isIndeterminate = true))
}

fun MutableSharedFlow<ProgressData>.tryMakeIndeterminate() {
	tryEmit(ProgressData(isIndeterminate = true))
}