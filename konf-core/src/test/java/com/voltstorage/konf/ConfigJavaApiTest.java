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

package com.voltstorage.konf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.voltstorage.konf.source.Source;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("test Java API of Config")
class ConfigJavaApiTest {
  private Config config;

  @BeforeEach
  void initConfig() {
    config = Configs.create();
    config.addSpec(NetworkBufferInJava.spec);
  }

  @Test
  @DisplayName("test `Configs.create`")
  void create() {
    final Config config = Configs.create();
    assertThat(config.getItems().size(), equalTo(0));
  }

  @Test
  @DisplayName("test `Configs.create` with init block")
  void createWithInit() {
    final Config config = Configs.create(it -> it.addSpec(NetworkBufferInJava.spec));
    assertThat(config.getItems().size(), equalTo(5));
  }

  @Test
  @DisplayName("test fluent API to load from map")
  void loadFromMap() {
    final HashMap<String, Integer> map = new HashMap<>();
    map.put(config.nameOf(NetworkBufferInJava.size), 1024);
    final Config newConfig = config.from().map.kv(map);
    assertThat(newConfig.get(NetworkBufferInJava.size), equalTo(1024));
  }

  @Test
  @DisplayName("test fluent API to load from system properties")
  void loadFromSystem() {
    System.setProperty(config.nameOf(NetworkBufferInJava.size), "1024");
    final Config newConfig = config.from().systemProperties();
    assertThat(newConfig.get(NetworkBufferInJava.size), equalTo(1024));
  }

  @Test
  @DisplayName("test fluent API to load from source")
  void loadFromSource() {
    final HashMap<String, Integer> map = new HashMap<>();
    map.put(config.nameOf(NetworkBufferInJava.size), 1024);
    final Config newConfig = config.withSource(Source.from().map.kv(map));
    assertThat(newConfig.get(NetworkBufferInJava.size), equalTo(1024));
  }

  @Test
  @DisplayName("test `get(Item<T>)`")
  void getWithItem() {
    final String name = config.get(NetworkBufferInJava.name);
    assertThat(name, equalTo("buffer"));
  }

  @Test
  @DisplayName("test `get(String)`")
  void getWithName() {
    final NetworkBuffer.Type type = config.get(config.nameOf(NetworkBufferInJava.type));
    assertThat(type, equalTo(NetworkBuffer.Type.OFF_HEAP));
  }

  @Test
  @DisplayName("test `set(Item<T>, T)`")
  void setWithItem() {
    config.set(NetworkBufferInJava.size, 1024);
    assertThat(config.get(NetworkBufferInJava.size), equalTo(1024));
  }

  @Test
  @DisplayName("test `set(String, T)`")
  void setWithName() {
    config.set(config.nameOf(NetworkBufferInJava.size), 1024);
    assertThat(config.get(NetworkBufferInJava.size), equalTo(1024));
  }

  @Test
  @DisplayName("test `lazySet(Item<T>, Function1<ItemContainer, T>)`")
  void lazySetWithItem() {
    config.lazySet(NetworkBufferInJava.maxSize, it -> it.get(NetworkBufferInJava.size) * 4);
    config.set(NetworkBufferInJava.size, 1024);
    assertThat(config.get(NetworkBufferInJava.maxSize), equalTo(1024 * 4));
  }

  @Test
  @DisplayName("test `lazySet(String, Function1<ItemContainer, T>)`")
  void lazySetWithName() {
    config.lazySet(
        config.nameOf(NetworkBufferInJava.maxSize), it -> it.get(NetworkBufferInJava.size) * 4);
    config.set(NetworkBufferInJava.size, 1024);
    assertThat(config.get(NetworkBufferInJava.maxSize), equalTo(1024 * 4));
  }
}
