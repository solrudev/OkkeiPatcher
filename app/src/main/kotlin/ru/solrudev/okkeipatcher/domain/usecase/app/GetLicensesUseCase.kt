package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.License
import ru.solrudev.okkeipatcher.domain.repository.app.LicensesRepository
import javax.inject.Inject

interface GetLicensesUseCase {
	suspend operator fun invoke(): List<License>
}

class GetLicensesUseCaseImpl @Inject constructor(
	private val licensesRepository: LicensesRepository
) : GetLicensesUseCase {

	override suspend fun invoke() = licensesRepository.getLicenses()
}