package ru.solrudev.okkeipatcher.app.repository

interface ConnectivityRepository {
	fun startNetworkMonitoring()
	fun stopNetworkMonitoring()
	fun isNetworkAvailable(): Boolean
}