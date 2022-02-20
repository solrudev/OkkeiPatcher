package solru.okkeipatcher.domain.usecase

import java.io.File

interface GetAppUpdateFileUseCase {
	suspend operator fun invoke(): File
}