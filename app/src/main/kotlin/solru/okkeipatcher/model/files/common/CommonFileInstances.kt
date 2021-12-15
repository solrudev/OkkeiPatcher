package solru.okkeipatcher.model.files.common

import android.os.Build
import solru.okkeipatcher.io.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import javax.inject.Inject

class CommonFileInstances @Inject constructor(ioService: IoService) {

	val backupApk: VerifiableFile by lazy { BackupApk(ioService) }
	val backupObb: VerifiableFile by lazy { BackupObb(ioService) }
	val backupSaveData: VerifiableFile by lazy { BackupSaveData(ioService) }
	val obbToBackup: VerifiableFile by lazy { ObbToBackup(ioService) }
	val obbToPatch: VerifiableFile by lazy { ObbToPatch(ioService) }

	val originalSaveData: VerifiableFile by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			OriginalSaveData.DocumentFileImpl(ioService)
		} else {
			OriginalSaveData.JavaFileImpl(ioService)
		}
	}

	val signedApk: VerifiableFile by lazy { SignedApk(ioService) }
	val tempApk: VerifiableFile by lazy { TempApk(ioService) }
	val tempSaveData: VerifiableFile by lazy { TempSaveData(ioService) }
}