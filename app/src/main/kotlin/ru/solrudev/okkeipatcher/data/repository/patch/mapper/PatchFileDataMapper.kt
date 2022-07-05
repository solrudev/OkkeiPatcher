package ru.solrudev.okkeipatcher.data.repository.patch.mapper

import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.domain.model.PatchFileData

fun FileDto.toPatchFileData() = PatchFileData(version, url, hash, size)