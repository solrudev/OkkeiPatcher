package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.app.repository.ConnectivityRepository
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor(@ApplicationContext applicationContext: Context) :
	ConnectivityRepository {

	private val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()
	private var isNetworkAvailable = false

	private val networkCallback by lazy {
		@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
		object : ConnectivityManager.NetworkCallback() {

			override fun onAvailable(network: Network) {
				isNetworkAvailable = true
			}

			override fun onLost(network: Network) {
				isNetworkAvailable = false
			}
		}
	}

	override fun startNetworkMonitoring() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			val networkRequest = NetworkRequest.Builder().apply {
				addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
				}
				addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
				addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
				addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
			}.build()
			connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
		}
	}

	override fun stopNetworkMonitoring() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			connectivityManager?.unregisterNetworkCallback(networkCallback)
		}
	}

	override fun isNetworkAvailable() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		isNetworkAvailable
	} else {
		isActiveNetworkConnected()
	}

	@Suppress("DEPRECATION")
	private fun isActiveNetworkConnected(): Boolean {
		val activeNetworkInfo = connectivityManager?.activeNetworkInfo
		return activeNetworkInfo != null && activeNetworkInfo.isConnected
	}
}