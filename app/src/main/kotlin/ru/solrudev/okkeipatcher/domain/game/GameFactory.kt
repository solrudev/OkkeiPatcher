package ru.solrudev.okkeipatcher.domain.game

import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import javax.inject.Inject
import javax.inject.Provider

class GameFactory @Inject constructor(
	private val patchStateRepository: PatchStateRepository,
	private val games: Map<Language, @JvmSuppressWildcards Provider<Game>>
) : SuspendFactory<Game> {

	override suspend fun create(): Game {
		val patchLanguage = patchStateRepository.patchLanguage.retrieve()
		return games.getValue(patchLanguage).get()
	}
}