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

package com.voltstorage.konf

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import com.natpryce.hamkrest.throws
import com.voltstorage.konf.source.LoadException
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike
import kotlin.collections.plus

object MultiLayerConfigSpec : SubjectSpek<Config>({

    subject { Config.Companion { addSpec(NetworkBuffer) }.withLayer("multi-layer") }

    itBehavesLike(ConfigTestSpec)

    group("multi-layer config") {
        it("should have specified name") {
            assertThat(subject.name, equalTo("multi-layer"))
        }
        it("should contain same items with parent config") {
            assertThat(
                subject[NetworkBuffer.name],
                equalTo(subject.parent!![NetworkBuffer.name]),
            )
            assertThat(
                subject[NetworkBuffer.type],
                equalTo(subject.parent!![NetworkBuffer.type]),
            )
            assertThat(
                subject[NetworkBuffer.offset],
                equalTo(subject.parent!![NetworkBuffer.offset]),
            )
        }
        on("set with item") {
            subject[NetworkBuffer.name] = "newName"
            it(
                "should contain the specified value in the top level," +
                    " and keep the rest levels unchanged",
            ) {
                assertThat(subject[NetworkBuffer.name], equalTo("newName"))
                assertThat(subject.parent!![NetworkBuffer.name], equalTo("buffer"))
            }
        }
        on("set with name") {
            subject[subject.nameOf(NetworkBuffer.name)] = "newName"
            it(
                "should contain the specified value in the top level," +
                    " and keep the rest levels unchanged",
            ) {
                assertThat(subject[NetworkBuffer.name], equalTo("newName"))
                assertThat(subject.parent!![NetworkBuffer.name], equalTo("buffer"))
            }
        }
        on("set parent's value") {
            subject.parent!![NetworkBuffer.name] = "newName"
            it("should contain the specified value in both top and parent level") {
                assertThat(subject[NetworkBuffer.name], equalTo("newName"))
                assertThat(subject.parent!![NetworkBuffer.name], equalTo("newName"))
            }
        }
        on("add spec") {
            val spec =
                object : ConfigSpec(NetworkBuffer.Companion.prefix) {
                    val minSize by optional(1)
                }
            subject.addSpec(spec)
            it("should contain items in new spec, and keep the rest level unchanged") {
                assertThat(spec.minSize in subject, equalTo(true))
                assertThat(subject.nameOf(spec.minSize) in subject, equalTo(true))
                assertThat(spec.minSize !in subject.parent!!, equalTo(true))
                assertThat(subject.nameOf(spec.minSize) !in subject.parent!!, equalTo(true))
            }
        }
        on("add spec to parent") {
            val spec =
                object : ConfigSpec(NetworkBuffer.Companion.prefix) {
                    @Suppress("unused")
                    val minSize by optional(1)
                }
            it("should throw LayerFrozenException") {
                assertThat({ subject.parent!!.addSpec(spec) }, throws<LayerFrozenException>())
            }
        }
        on("add item to parent") {
            val minSize by Spec.Companion.dummy.optional(1)
            it("should throw LayerFrozenException") {
                assertThat({ subject.parent!!.addItem(minSize) }, throws<LayerFrozenException>())
            }
        }
        on("iterate items in config after adding spec") {
            val spec =
                object : ConfigSpec(NetworkBuffer.Companion.prefix) {
                    @Suppress("unused")
                    val minSize by optional(1)
                }
            subject.addSpec(spec)
            it("should cover all items in config") {
                assertThat(
                    subject.iterator().asSequence().toSet(),
                    equalTo((NetworkBuffer.Companion.items + spec.items).toSet()),
                )
            }
        }
        on("add custom deserializer to mapper in parent") {
            it("should throw LoadException before adding deserializer") {
                val spec =
                    object : ConfigSpec() {
                        @Suppress("unused")
                        val item by required<StringWrapper>()
                    }
                val parent = Config.Companion { addSpec(spec) }
                val child = parent.withLayer("child")

                assertThat(parent.mapper, sameInstance(child.mapper))
                assertThat(
                    { child.from.map.kv(mapOf("item" to "string")) },
                    throws<LoadException>(),
                )
            }
            it("should be able to use the specified deserializer after adding") {
                val spec =
                    object : ConfigSpec() {
                        val item by required<StringWrapper>()
                    }
                val parent = Config.Companion { addSpec(spec) }
                val child = parent.withLayer("child")

                assertThat(parent.mapper, sameInstance(child.mapper))
                parent.mapper.registerModule(
                    SimpleModule().apply {
                        addDeserializer(StringWrapper::class.java, StringWrapperDeserializer())
                    },
                )
                val afterLoad = child.from.map.kv(mapOf("item" to "string"))
                assertThat(child.mapper, sameInstance(afterLoad.mapper))
                assertThat(afterLoad[spec.item], equalTo(StringWrapper("string")))
            }
        }
    }
})

private data class StringWrapper(val string: String)

private class StringWrapperDeserializer :
    StdDeserializer<StringWrapper>(StringWrapper::class.java) {
    override fun deserialize(
        jp: JsonParser,
        ctxt: DeserializationContext,
    ): StringWrapper {
        val node = jp.codec.readTree<JsonNode>(jp)
        return StringWrapper(node.textValue())
    }
}
