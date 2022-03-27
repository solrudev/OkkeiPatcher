package solru.okkeipatcher.domain.file.common

import android.os.Build
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import javax.inject.Inject

class CommonFiles @Inject constructor(streamCopier: StreamCopier) {

	val backupApk: VerifiableFile by lazy { BackupApk(streamCopier) }
	val backupObb: VerifiableFile by lazy { BackupObb(streamCopier) }
	val backupSaveData: VerifiableFile by lazy { BackupSaveData(streamCopier) }
	val obbToBackup: VerifiableFile by lazy { ObbToBackup(streamCopier) }
	val obbToPatch: VerifiableFile by lazy { ObbToPatch(streamCopier) }

	val originalSaveData: VerifiableFile by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			OriginalSaveData.DocumentFileImpl(streamCopier)
		} else {
			OriginalSaveData.JavaFileImpl(streamCopier)
		}
	}

	val signedApk: VerifiableFile by lazy { SignedApk(streamCopier) }
	val tempApk: VerifiableFile by lazy { TempApk(streamCopier) }
	val tempSaveData: VerifiableFile by lazy { TempSaveData(streamCopier) }
}