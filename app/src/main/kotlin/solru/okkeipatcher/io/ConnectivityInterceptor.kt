package solru.okkeipatcher.io

import okhttp3.Interceptor
import okhttp3.Response
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.io.exception.NetworkNotAvailableException

class ConnectivityInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response = if (OkkeiApplication.isNetworkAvailable) {
		chain.proceed(chain.request())
	} else {
		throw NetworkNotAvailableException()
	}
}