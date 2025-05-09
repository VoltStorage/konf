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

package io.github.voltstorage.konf.source.toml

import io.github.voltstorage.konf.Config
import io.github.voltstorage.konf.Feature
import io.github.voltstorage.konf.source.ConfigForLoad
import io.github.voltstorage.konf.source.SourceLoadBaseSpec
import io.github.voltstorage.konf.source.toml
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

object TomlSourceLoadSpec : SubjectSpek<Config>({

    subject {
        Config {
            addSpec(ConfigForLoad)
            enable(Feature.FAIL_ON_UNKNOWN_PATH)
        }.from.toml.resource("source/source.toml")
    }

    itBehavesLike(SourceLoadBaseSpec)
})

object TomlSourceReloadSpec : SubjectSpek<Config>({

    subject {
        val config =
            Config {
                addSpec(ConfigForLoad)
            }.from.toml.resource("source/source.toml")
        val toml = config.toToml.toText()
        Config {
            addSpec(ConfigForLoad)
        }.from.toml.string(toml)
    }

    itBehavesLike(SourceLoadBaseSpec)
})
