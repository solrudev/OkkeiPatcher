package ru.solrudev.okkeipatcher.io

import okhttp3.Interceptor
import okhttp3.Response
import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException

class ConnectivityInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response = if (OkkeiApplication.isNetworkAvailable) {
		chain.proceed(chain.request())
	} else {
		throw NetworkNotAvailableException()
	}
}