//TODO

package ru.solrudev.okkeipatcher.domain.usecase.app

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import java.util.*
import javax.inject.Inject

interface GetAppChangelogUseCase {
	suspend operator fun invoke(): OkkeiPatcherChangelogDto
}

class GetAppChangelogUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository,
	@ApplicationContext private val applicationContext: Context
) : GetAppChangelogUseCase {

	override suspend fun invoke(): OkkeiPatcherChangelogDto {
		val locale = ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0]
		return okkeiPatcherRepository.getChangelog(locale ?: Locale.ENGLISH)
	}
}