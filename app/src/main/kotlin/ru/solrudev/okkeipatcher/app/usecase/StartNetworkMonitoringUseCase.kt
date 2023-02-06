package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.ConnectivityRepository
import javax.inject.Inject

class StartNetworkMonitoringUseCase @Inject constructor(private val connectivityRepository: ConnectivityRepository) {
	operator fun invoke() = connectivityRepository.startNetworkMonitoring()
}