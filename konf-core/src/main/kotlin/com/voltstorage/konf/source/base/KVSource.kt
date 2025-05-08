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

package com.voltstorage.konf.source.base

import com.voltstorage.konf.ContainerNode
import com.voltstorage.konf.TreeNode
import com.voltstorage.konf.notEmptyOr
import com.voltstorage.konf.source.SourceInfo
import com.voltstorage.konf.source.asTree

/**
 * Source from a map in key-value format.
 */
open class KVSource(
    val map: Map<String, Any>,
    type: String = "",
    info: SourceInfo = SourceInfo(),
) : ValueSource(map, type.notEmptyOr("KV"), info) {
    override val tree: TreeNode = map.kvToTree()
}

fun Map<String, Any>.kvToTree(): TreeNode {
    return ContainerNode(mutableMapOf()).apply {
        this@kvToTree.forEach { (path, value) ->
            set(path, value.asTree())
        }
    }
}

fun Map<String, Any>.asKVSource() = KVSource(this)
