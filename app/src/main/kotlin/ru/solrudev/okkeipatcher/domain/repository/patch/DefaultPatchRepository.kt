package ru.solrudev.okkeipatcher.domain.repository.patch

interface DefaultPatchRepository : PatchRepository {
	val scripts: PatchFile
	val obb: PatchFile
}