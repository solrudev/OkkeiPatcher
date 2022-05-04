package ru.solrudev.okkeipatcher.domain.factory

interface Factory<out T> {
	suspend fun create(): T
}