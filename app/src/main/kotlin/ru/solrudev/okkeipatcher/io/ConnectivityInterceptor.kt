package ru.solrudev.okkeipatcher.io

import okhttp3.Interceptor
import okhttp3.Response
import ru.solrudev.okkeipatcher.domain.repository.app.ConnectivityRepository
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(private val connectivityRepository: ConnectivityRepository) :
	Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response = if (connectivityRepository.isNetworkAvailable()) {
		chain.proceed(chain.request())
	} else {
		throw NetworkNotAvailableException()
	}
}