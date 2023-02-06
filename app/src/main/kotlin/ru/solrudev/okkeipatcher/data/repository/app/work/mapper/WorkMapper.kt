package ru.solrudev.okkeipatcher.data.repository.app.work.mapper

import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

fun WorkInfo.toWork(label: LocalizedString) = Work(id, label)