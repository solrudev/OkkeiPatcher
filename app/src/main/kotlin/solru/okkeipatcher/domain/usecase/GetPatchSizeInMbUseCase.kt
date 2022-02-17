package solru.okkeipatcher.domain.usecase

interface GetPatchSizeInMbUseCase {
	suspend operator fun invoke(): Double
}