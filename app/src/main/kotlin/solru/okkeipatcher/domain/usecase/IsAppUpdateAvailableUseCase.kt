package solru.okkeipatcher.domain.usecase

interface IsAppUpdateAvailableUseCase {
	suspend operator fun invoke(): Boolean
}