package ru.solrudev.okkeipatcher.app.repository

import ru.solrudev.okkeipatcher.app.model.License

interface LicensesRepository {
	suspend fun getLicenses(): List<License>
}