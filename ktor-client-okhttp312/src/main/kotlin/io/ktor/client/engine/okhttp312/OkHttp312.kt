/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.engine.okhttp312

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.util.*

/**
 * [HttpClientEngineFactory] using a [OkHttp312] based backend implementation
 * with the the associated configuration [OkHttpConfig].
 */
public object OkHttp312 : HttpClientEngineFactory<OkHttpConfig> {
    @OptIn(InternalAPI::class)
    override fun create(block: OkHttpConfig.() -> Unit): HttpClientEngine =
        OkHttpEngine(OkHttpConfig().apply(block))
}

@Suppress("KDocMissingDocumentation")
public class OkHttpEngineContainer : HttpClientEngineContainer {
    override val factory: HttpClientEngineFactory<*> = OkHttp312

    override fun toString(): String = "OkHttp"
}
