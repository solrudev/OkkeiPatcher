package ru.solrudev.okkeipatcher.ui.core

import kotlin.properties.ReadOnlyProperty

interface DerivedViewProperty<in V, in S : UiState, out DV : FeatureView<S>> : ReadOnlyProperty<V, DV>, (V) -> DV