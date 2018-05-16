/*
 * Copyright (C) 2018 justlive1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.cloud.admin.server.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MapConfig;
import lombok.Data;

/**
 * Hazelcast配置
 * 
 * @author wubo
 *
 */
@Configuration
@ConditionalOnProperty(name = "spring.boot.admin.hazelcast.enabled", havingValue = "true")
public class HazelcastConf {

  @Data
  @Configuration
  @ConfigurationProperties(prefix = "spring.boot.admin.hazelcast")
  class HazelcastProps {
    String applicationStore;
    Integer applicationBackupCount = 1;
    String applicationEvictionPolicy = "NONE";
    String eventStore;
    Integer eventStoreBackupCount = 1;
    Integer eventStoreMaxSize = 1_000;
  }

  @Bean
  public Config hazelcastConfig(HazelcastProps props) {

    return new Config().setProperty("hazelcast.jmx", "true")
        .addMapConfig(
            new MapConfig(props.applicationStore).setBackupCount(props.applicationBackupCount)
                .setEvictionPolicy(EvictionPolicy.valueOf(props.applicationEvictionPolicy)))
        .addListConfig(new ListConfig(props.eventStore).setBackupCount(props.eventStoreBackupCount)
            .setMaxSize(props.eventStoreMaxSize));
  }
}
