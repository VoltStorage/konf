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

package com.voltstorage.konf.snippet;

import com.voltstorage.konf.Config;

public class ServerInJava {
  private String host;
  private Integer tcpPort;

  public ServerInJava(String host, Integer tcpPort) {
    this.host = host;
    this.tcpPort = tcpPort;
  }

  public ServerInJava(Config config) {
    this(config.get(ServerSpecInJava.host), config.get(ServerSpecInJava.tcpPort));
  }

  public String getHost() {
    return host;
  }

  public Integer getTcpPort() {
    return tcpPort;
  }
}
