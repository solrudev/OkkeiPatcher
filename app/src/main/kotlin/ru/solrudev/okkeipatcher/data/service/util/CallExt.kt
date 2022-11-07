package ru.solrudev.okkeipatcher.data.service.util

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

suspend fun Call.await(): Response {
	return suspendCancellableCoroutine { continuation ->
		enqueue(object : Callback {

			override fun onResponse(call: Call, response: Response) {
				continuation.resume(response)
			}

			override fun onFailure(call: Call, e: IOException) {
				if (continuation.isCancelled) {
					return
				}
				continuation.cancel(e)
			}
		})
		continuation.invokeOnCancellation {
			try {
				cancel()
			} catch (_: Throwable) {
			}
		}
	}
}