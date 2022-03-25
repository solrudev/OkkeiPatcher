package solru.okkeipatcher.data.repository.patch

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solru.okkeipatcher.data.network.api.patch.DefaultPatchApi
import solru.okkeipatcher.data.network.model.patch.DefaultPatchDto
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultPatchRepositoryImpl @Inject constructor(private val defaultPatchApi: DefaultPatchApi) :
	DefaultPatchRepository {

	private val patchDataMutex = Mutex()
	private var patchDataCache: DefaultPatchDto? = null

	override suspend fun getScriptsData() = retrievePatchData().scripts
	override suspend fun getObbData() = retrievePatchData().obb

	private suspend inline fun retrievePatchData(): DefaultPatchDto {
		patchDataMutex.withLock {
			patchDataCache?.let { return it }
			return defaultPatchApi.getPatchData().also { patchDataCache = it }
		}
	}
}