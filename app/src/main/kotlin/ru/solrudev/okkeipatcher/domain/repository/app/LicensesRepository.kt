package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.model.License

interface LicensesRepository {
	suspend fun getLicenses(): List<License>
}