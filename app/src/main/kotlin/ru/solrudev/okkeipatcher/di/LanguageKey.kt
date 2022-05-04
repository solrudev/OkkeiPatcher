package ru.solrudev.okkeipatcher.di

import dagger.MapKey
import ru.solrudev.okkeipatcher.domain.model.Language

@MapKey
annotation class LanguageKey(val language: Language)