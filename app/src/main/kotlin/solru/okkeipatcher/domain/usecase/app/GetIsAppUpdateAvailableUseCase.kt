package solru.okkeipatcher.domain.usecase.app

interface GetIsAppUpdateAvailableUseCase {
	suspend operator fun invoke(): Boolean
}