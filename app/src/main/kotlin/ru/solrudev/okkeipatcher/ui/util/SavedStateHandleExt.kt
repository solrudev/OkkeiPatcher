package ru.solrudev.okkeipatcher.ui.util

import androidx.lifecycle.SavedStateHandle

private const val RESULT = "result"

fun SavedStateHandle.getResult() = get<Boolean>(RESULT)
fun SavedStateHandle.setResult(value: Boolean) = set(RESULT, value)
fun SavedStateHandle.clearResult() = remove<Boolean>(RESULT)