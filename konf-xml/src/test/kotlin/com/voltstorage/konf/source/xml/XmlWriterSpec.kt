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

package com.voltstorage.konf.source.xml

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.voltstorage.konf.Config
import com.voltstorage.konf.ConfigSpec
import com.voltstorage.konf.source.Writer
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.io.ByteArrayOutputStream
import java.io.StringWriter

object XmlWriterSpec : SubjectSpek<Writer>({
    subject {
        val config =
            Config {
                addSpec(
                    object : ConfigSpec() {
                        val key by optional("value")
                    },
                )
            }
        config.toXml
    }

    given("a writer") {
        val expectedString =
            """
            |<?xml version="1.0" encoding="UTF-8"?>
            |
            |<configuration>
            |  <property>
            |    <name>key</name>
            |    <value>value</value>
            |  </property>
            |</configuration>
            |
            """.trimMargin().replace("\n", System.lineSeparator())
        on("save to writer") {
            val writer = StringWriter()
            subject.toWriter(writer)
            it("should return a writer which contains content from config") {
                assertThat(writer.toString(), equalTo(expectedString))
            }
        }
        on("save to output stream") {
            val outputStream = ByteArrayOutputStream()
            subject.toOutputStream(outputStream)
            it("should return an output stream which contains content from config") {
                assertThat(outputStream.toString(), equalTo(expectedString))
            }
        }
    }
})
