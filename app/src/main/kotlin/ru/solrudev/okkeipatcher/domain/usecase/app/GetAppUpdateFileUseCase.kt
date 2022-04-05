package ru.solrudev.okkeipatcher.domain.usecase.app

import java.io.File

interface GetAppUpdateFileUseCase {
	suspend operator fun invoke(): File
}