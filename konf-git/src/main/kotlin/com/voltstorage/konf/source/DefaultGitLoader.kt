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

import com.voltstorage.konf.Config
import kotlinx.coroutines.Dispatchers
import org.eclipse.jgit.lib.Constants
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * Returns a child config containing values from a specified git repository.
 *
 * Format of the url is auto-detected from the url extension.
 * Supported url formats and the corresponding extensions:
 * - HOCON: conf
 * - JSON: json
 * - Properties: properties
 * - TOML: toml
 * - XML: xml
 * - YAML: yml, yaml
 *
 * Throws [UnsupportedExtensionException] if the url extension is unsupported.
 *
 * @param repo git repository
 * @param file file in the git repository
 * @param dir local directory of the git repository
 * @param branch the initial branch
 * @param optional whether the source is optional
 * @param action additional action when cloning/pulling
 * @return a child config containing values from a specified git repository
 * @throws UnsupportedExtensionException
 */
fun DefaultLoaders.git(
    repo: String,
    file: String,
    dir: String? = null,
    branch: String = Constants.HEAD,
    optional: Boolean = this.optional,
): Config =
    dispatchExtension(File(file).extension, "{repo: $repo, file: $file}")
        .git(repo, file, dir, branch, optional)

/**
 * Returns a child config containing values from a specified git repository,
 * and reloads values periodically.
 *
 * Format of the url is auto-detected from the url extension.
 * Supported url formats and the corresponding extensions:
 * - HOCON: conf
 * - JSON: json
 * - Properties: properties
 * - TOML: toml
 * - XML: xml
 * - YAML: yml, yaml
 *
 * Throws [UnsupportedExtensionException] if the url extension is unsupported.
 *
 * @param repo git repository
 * @param file file in the git repository
 * @param dir local directory of the git repository
 * @param branch the initial branch
 * @param period reload period. The default value is 1.
 * @param unit time unit of reload period. The default value is [TimeUnit.MINUTES].
 * @param context context of the coroutine. The default value is [Dispatchers.Default].
 * @param optional whether the source is optional
 * @param onLoad function invoked after the updated git file is loaded
 * @return a child config containing values from a specified git repository
 * @throws UnsupportedExtensionException
 */
fun DefaultLoaders.watchGit(
    repo: String,
    file: String,
    dir: String? = null,
    branch: String = Constants.HEAD,
    period: Long = 1,
    unit: TimeUnit = TimeUnit.MINUTES,
    context: CoroutineContext = Dispatchers.Default,
    optional: Boolean = this.optional,
    onLoad: ((config: Config, source: Source) -> Unit)? = null,
): Config =
    dispatchExtension(File(file).extension, "{repo: $repo, file: $file}")
        .watchGit(repo, file, dir, branch, period, unit, context, optional, onLoad)
