/*
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.voltstorage.konf.source.properties

import io.github.voltstorage.konf.annotation.JavaApi
import io.github.voltstorage.konf.source.Provider
import io.github.voltstorage.konf.source.Source
import io.github.voltstorage.konf.source.base.FlatSource
import java.io.InputStream
import java.io.Reader
import java.util.Properties

/**
 * Provider for properties source.
 */
object PropertiesProvider : Provider {
    @Suppress("UNCHECKED_CAST")
    private fun Properties.toMap(): Map<String, String> = this as Map<String, String>

    override fun reader(reader: Reader): Source = FlatSource(Properties().apply { load(reader) }.toMap(), type = "properties")

    override fun inputStream(inputStream: InputStream): Source =
        FlatSource(Properties().apply { load(inputStream) }.toMap(), type = "properties")

    /**
     * Returns a new source from system properties.
     *
     * @return a new source from system properties
     */
    fun system(): Source =
        FlatSource(
            System.getProperties().toMap(),
            type = "system-properties",
            allowConflict = true,
        )

    @JavaApi
    @JvmStatic
    fun get() = this
}
