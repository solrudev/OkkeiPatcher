package solru.okkeipatcher.model.files.common

import android.os.Build
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import javax.inject.Inject

class CommonFileInstances @Inject constructor(ioService: IoService) {

	val backupApk: VerifiableFileWrapper by lazy { BackupApk(ioService) }
	val backupObb: VerifiableFileWrapper by lazy { BackupObb(ioService) }
	val backupSaveData: VerifiableFileWrapper by lazy { BackupSaveData(ioService) }
	val obbToBackup: VerifiableFileWrapper by lazy { ObbToBackup(ioService) }
	val obbToPatch: VerifiableFileWrapper by lazy { ObbToPatch(ioService) }

	val originalSaveData: VerifiableFileWrapper by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			OriginalSaveData.SafFileImpl(ioService)
		} else {
			OriginalSaveData.JavaFileImpl(ioService)
		}
	}

	val signedApk: VerifiableFileWrapper by lazy { SignedApk(ioService) }
	val tempApk: VerifiableFileWrapper by lazy { TempApk(ioService) }
	val tempSaveData: VerifiableFileWrapper by lazy { TempSaveData(ioService) }
}