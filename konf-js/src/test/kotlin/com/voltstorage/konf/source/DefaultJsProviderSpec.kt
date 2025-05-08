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

package com.voltstorage.konf.source

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.voltstorage.konf.tempFileOf
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object DefaultJsProviderSpec : SubjectSpek<DefaultProviders>({
    subject { Source.from }

    val item = DefaultLoadersConfig.type

    given("a provider") {
        on("provider source from JavaScript file") {
            val config = subject.file(tempFileOf(JS_CONTENT, suffix = ".js")).toConfig()
            it("should provide as auto-detected file format") {
                assertThat(config[item], equalTo("js"))
            }
        }
    }
})
