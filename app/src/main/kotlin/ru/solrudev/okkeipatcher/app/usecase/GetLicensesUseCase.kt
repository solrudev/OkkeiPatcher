package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.LicensesRepository
import javax.inject.Inject

class GetLicensesUseCase @Inject constructor(private val licensesRepository: LicensesRepository) {
	suspend operator fun invoke() = licensesRepository.getLicenses()
}