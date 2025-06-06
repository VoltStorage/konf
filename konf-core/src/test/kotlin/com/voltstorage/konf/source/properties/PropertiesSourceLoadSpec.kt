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

package com.voltstorage.konf.source.properties

import com.voltstorage.konf.Config
import com.voltstorage.konf.source.ConfigForLoad
import com.voltstorage.konf.source.base.FlatConfigForLoad
import com.voltstorage.konf.source.base.FlatSourceLoadBaseSpec
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

object PropertiesSourceLoadSpec : SubjectSpek<Config>({

    subject {
        Config {
            addSpec(ConfigForLoad)
            addSpec(FlatConfigForLoad)
        }.from.properties.resource("source/source.properties")
    }

    itBehavesLike(FlatSourceLoadBaseSpec)
})

object PropertiesSourceReloadSpec : SubjectSpek<Config>({

    subject {
        val config =
            Config {
                addSpec(ConfigForLoad)
                addSpec(FlatConfigForLoad)
            }.from.properties.resource("source/source.properties")
        val properties = config.toProperties.toText()
        Config {
            addSpec(ConfigForLoad)
            addSpec(FlatConfigForLoad)
        }.from.properties.string(properties)
    }

    itBehavesLike(FlatSourceLoadBaseSpec)
})
