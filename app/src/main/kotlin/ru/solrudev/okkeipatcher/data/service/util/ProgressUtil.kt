package ru.solrudev.okkeipatcher.data.service.util

import kotlin.math.ceil
import kotlin.math.roundToInt

data class Progress(
	val progressDelta: Int,
	val progressRatio: Int
)

fun calculateProgress(totalSize: Long, bufferLength: Long, progressMax: Int): Progress {
	val ratio = ceil(totalSize.toDouble() / (bufferLength * progressMax)).toInt().coerceAtLeast(1)
	val max = ceil(totalSize.toDouble() / (bufferLength * ratio)).toInt()
	val delta = (progressMax.toDouble() / max).roundToInt()
	return Progress(delta, ratio)
}