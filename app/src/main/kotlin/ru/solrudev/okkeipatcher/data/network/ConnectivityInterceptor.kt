package ru.solrudev.okkeipatcher.data.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.domain.repository.app.ConnectivityRepository
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(private val connectivityRepository: ConnectivityRepository) :
	Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response = if (connectivityRepository.isNetworkAvailable()) {
		chain.proceed(chain.request())
	} else {
		throw NetworkNotAvailableException()
	}
}