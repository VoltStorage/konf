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

package com.voltstorage.konf.snippet

import com.voltstorage.konf.Config

fun main(args: Array<String>) {
    val config = Config { addSpec(Server) }
    config[Server.tcpPort] = 1000
    // fork from parent config
    val childConfig = config.withLayer("child")
    // child config inherit values from parent config
    check(childConfig[Server.tcpPort] == 1000)
    // modifications in parent config affect values in child config
    config[Server.tcpPort] = 2000
    check(config[Server.tcpPort] == 2000)
    check(childConfig[Server.tcpPort] == 2000)
    // modifications in child config don't affect values in parent config
    childConfig[Server.tcpPort] = 3000
    check(config[Server.tcpPort] == 2000)
    check(childConfig[Server.tcpPort] == 3000)
}
