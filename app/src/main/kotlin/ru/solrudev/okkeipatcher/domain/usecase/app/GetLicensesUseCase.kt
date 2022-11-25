package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.LicensesRepository
import javax.inject.Inject

class GetLicensesUseCase @Inject constructor(private val licensesRepository: LicensesRepository) {
	suspend operator fun invoke() = licensesRepository.getLicenses()
}