package ru.solrudev.okkeipatcher.domain.file

import android.os.Build
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import javax.inject.Inject

class CommonFiles @Inject constructor(streamCopier: StreamCopier) {

	val backupSaveData: VerifiableFile by lazy { BackupSaveData(streamCopier) }

	val originalSaveData: VerifiableFile by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			OriginalSaveData.DocumentFileImpl(streamCopier)
		} else {
			OriginalSaveData.JavaFileImpl(streamCopier)
		}
	}

	val tempSaveData: VerifiableFile by lazy { TempSaveData(streamCopier) }
}