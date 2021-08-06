package io.ktor.client.engine.okhttp312.backport

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util.checkOffsetAndCount
import okio.BufferedSink

@JvmOverloads
@JvmName("create")
fun ByteArray.toRequestBody(
	contentType: MediaType? = null,
	offset: Int = 0,
	byteCount: Int = size
): RequestBody {
	checkOffsetAndCount(size.toLong(), offset.toLong(), byteCount.toLong())
	return object : RequestBody() {
		override fun contentType() = contentType

		override fun contentLength() = byteCount.toLong()

		override fun writeTo(sink: BufferedSink) {
			sink.write(this@toRequestBody, offset, byteCount)
		}
	}
}