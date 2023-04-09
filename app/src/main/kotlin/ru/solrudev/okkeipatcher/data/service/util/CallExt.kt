/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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