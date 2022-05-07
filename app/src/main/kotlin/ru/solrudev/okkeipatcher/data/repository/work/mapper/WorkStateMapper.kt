package ru.solrudev.okkeipatcher.data.repository.work.mapper

import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.data.mapper.Mapper
import ru.solrudev.okkeipatcher.domain.model.WorkState

interface WorkStateMapper : Mapper<WorkInfo?, WorkState>