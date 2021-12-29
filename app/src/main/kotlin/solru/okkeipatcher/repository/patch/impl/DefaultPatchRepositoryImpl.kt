package solru.okkeipatcher.repository.patch.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solru.okkeipatcher.api.patchdata.DefaultPatchDataService
import solru.okkeipatcher.data.patchdata.DefaultPatchData
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultPatchRepositoryImpl @Inject constructor(private val defaultPatchDataService: DefaultPatchDataService) :
	DefaultPatchRepository {

	private val patchDataMutex = Mutex()
	private var patchDataCache: DefaultPatchData? = null

	override suspend fun getScriptsData() = retrievePatchData().scripts
	override suspend fun getObbData() = retrievePatchData().obb

	private suspend inline fun retrievePatchData(): DefaultPatchData {
		patchDataMutex.withLock {
			patchDataCache?.let { return it }
			return defaultPatchDataService.getPatchData().also { patchDataCache = it }
		}
	}
}