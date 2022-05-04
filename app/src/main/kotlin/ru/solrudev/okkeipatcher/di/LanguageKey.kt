package ru.solrudev.okkeipatcher.di

import dagger.MapKey
import ru.solrudev.okkeipatcher.domain.model.Language

@MapKey
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LanguageKey(val language: Language)