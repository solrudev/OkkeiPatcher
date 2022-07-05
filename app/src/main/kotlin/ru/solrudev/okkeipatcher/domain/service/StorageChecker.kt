package ru.solrudev.okkeipatcher.domain.service

interface StorageChecker {
	fun isEnoughSpace(): Boolean
}