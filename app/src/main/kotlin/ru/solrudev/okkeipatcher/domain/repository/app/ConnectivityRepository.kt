package ru.solrudev.okkeipatcher.domain.repository.app

interface ConnectivityRepository {
	fun startNetworkMonitoring()
	fun stopNetworkMonitoring()
	fun isNetworkAvailable(): Boolean
}