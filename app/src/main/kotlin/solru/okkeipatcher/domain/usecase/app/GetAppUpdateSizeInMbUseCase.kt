package solru.okkeipatcher.domain.usecase.app

interface GetAppUpdateSizeInMbUseCase {
	suspend operator fun invoke(): Double
}