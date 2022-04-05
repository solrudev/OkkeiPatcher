package ru.solrudev.okkeipatcher.domain.usecase.patch

interface GetPatchSizeInMbUseCase {
	suspend operator fun invoke(): Double
}