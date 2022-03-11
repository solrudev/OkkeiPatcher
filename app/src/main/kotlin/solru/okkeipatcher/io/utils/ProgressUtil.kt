package solru.okkeipatcher.io.utils

import kotlin.math.ceil

private const val EMIT_COUNT = 100
fun calculateProgressRatio(totalSize: Long, bufferLength: Long) =
	ceil(totalSize.toDouble() / (bufferLength * EMIT_COUNT)).toInt().coerceAtLeast(1)