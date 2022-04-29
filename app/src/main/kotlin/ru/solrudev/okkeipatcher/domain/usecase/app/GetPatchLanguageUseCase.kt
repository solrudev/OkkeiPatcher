package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.Language

interface GetPatchLanguageUseCase {
	suspend operator fun invoke(): Language
}