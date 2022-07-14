package ru.solrudev.okkeipatcher.data.repository.work.mapper

import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Work

fun WorkInfo.toWork(label: LocalizedString) = Work(id, label)