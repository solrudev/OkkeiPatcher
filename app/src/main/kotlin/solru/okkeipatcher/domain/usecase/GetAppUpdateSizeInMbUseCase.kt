package solru.okkeipatcher.domain.usecase

interface GetAppUpdateSizeInMbUseCase {
	suspend operator fun invoke(): Double
}