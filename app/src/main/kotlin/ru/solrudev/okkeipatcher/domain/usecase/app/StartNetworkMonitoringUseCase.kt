package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.ConnectivityRepository
import javax.inject.Inject

class StartNetworkMonitoringUseCase @Inject constructor(private val connectivityRepository: ConnectivityRepository) {
	operator fun invoke() = connectivityRepository.startNetworkMonitoring()
}