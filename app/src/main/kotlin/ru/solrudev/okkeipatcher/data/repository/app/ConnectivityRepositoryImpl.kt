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
import javax.inject.Singleton

@Singleton
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