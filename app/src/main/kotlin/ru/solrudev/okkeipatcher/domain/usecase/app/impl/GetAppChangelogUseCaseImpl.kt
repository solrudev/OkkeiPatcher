package ru.solrudev.okkeipatcher.domain.usecase.app.impl

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.usecase.app.GetAppChangelogUseCase
import javax.inject.Inject

class GetAppChangelogUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository,
	@ApplicationContext private val applicationContext: Context
) : GetAppChangelogUseCase {

	override suspend fun invoke(): OkkeiPatcherChangelogDto {
		val locale = ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0]
		return okkeiPatcherRepository.getChangelog(locale)
	}
}