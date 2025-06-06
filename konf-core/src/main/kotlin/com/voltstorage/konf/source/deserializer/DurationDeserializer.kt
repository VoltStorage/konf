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

import com.voltstorage.konf.source.SourceException
import com.voltstorage.konf.source.toDuration
import java.time.Duration
import java.time.format.DateTimeParseException

/**
 * Deserializer for [Duration].
 */
object DurationDeserializer : JSR310Deserializer<Duration>(Duration::class.java) {
    override fun parse(string: String): Duration {
        return try {
            Duration.parse(string)
        } catch (exception: DateTimeParseException) {
            try {
                string.toDuration()
            } catch (_: SourceException) {
                throw exception
            }
        }
    }
}
