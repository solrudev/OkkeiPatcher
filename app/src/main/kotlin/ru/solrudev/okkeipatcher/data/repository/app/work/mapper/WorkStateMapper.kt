package ru.solrudev.okkeipatcher.data.repository.app.work.mapper

import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.app.model.WorkState
import ru.solrudev.okkeipatcher.data.core.Mapper

interface WorkStateMapper : Mapper<WorkInfo?, WorkState>