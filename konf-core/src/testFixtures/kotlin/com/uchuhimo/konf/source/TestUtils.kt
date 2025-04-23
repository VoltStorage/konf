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

package com.uchuhimo.konf.source

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.throws
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec

object DefaultLoadersConfig : ConfigSpec("source.test") {
    val type by required<String>()
}

fun Source.toConfig(): Config =
    Config {
        addSpec(DefaultLoadersConfig)
    }.withSource(this)

inline fun <reified T : Any> assertCausedBy(noinline block: () -> Unit) {
    @Suppress("UNCHECKED_CAST")
    assertThat(
        block,
        throws(
            has(
                LoadException::cause,
                isA<T>() as Matcher<Throwable?>,
            ),
        ),
    )
}

const val PROPERTIES_CONTENT = "source.test.type = properties"
