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

package com.voltstorage.konf.source.deserializer

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.voltstorage.konf.Config
import com.voltstorage.konf.ConfigSpec
import com.voltstorage.konf.source.ObjectMappingException
import com.voltstorage.konf.source.assertCausedBy
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.time.ZonedDateTime

object ZonedDateTimeDeserializerSpec : Spek({
    val spec =
        object : ConfigSpec() {
            val item by required<ZonedDateTimeWrapper>()
        }
    val config by memoized {
        Config {
            addSpec(spec)
        }
    }

    given("an ZonedDateTime deserializer") {
        on("deserialize valid string") {
            config.from.map.kv(mapOf("item" to mapOf("zonedDateTime" to "2007-12-03T10:15:30+01:00[Europe/Paris]"))).apply {
                it("should succeed") {
                    assertThat(
                        this@apply[spec.item].zonedDateTime,
                        equalTo(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]")),
                    )
                }
            }
        }
        on("deserialize empty string") {
            it("should throw LoadException caused by ObjectMappingException") {
                assertCausedBy<ObjectMappingException> {
                    config.from.map.kv(mapOf("item" to mapOf("zonedDateTime" to "  ")))
                }
            }
        }
        on("deserialize value with invalid type") {
            it("should throw LoadException caused by ObjectMappingException") {
                assertCausedBy<ObjectMappingException> {
                    config.from.map.kv(mapOf("item" to mapOf("zonedDateTime" to 1)))
                }
            }
        }
        on("deserialize value with invalid format") {
            it("should throw LoadException caused by ObjectMappingException") {
                assertCausedBy<ObjectMappingException> {
                    config.from.map.kv(mapOf("item" to mapOf("zonedDateTime" to "2007-12-03T10:15:30")))
                }
            }
        }
    }
})

private data class ZonedDateTimeWrapper(val zonedDateTime: ZonedDateTime)
