package ru.solrudev.okkeipatcher.domain.usecase.app

interface GetIsPatchedUseCase {
	suspend operator fun invoke(): Boolean
}