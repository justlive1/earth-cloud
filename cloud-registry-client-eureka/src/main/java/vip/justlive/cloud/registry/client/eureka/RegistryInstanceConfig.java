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
package vip.justlive.cloud.registry.client.eureka;

import com.netflix.appinfo.MyDataCenterInstanceConfig;

/**
 * 统一命名空间
 * 
 * @author wubo
 *
 */
public class RegistryInstanceConfig extends MyDataCenterInstanceConfig {

  public RegistryInstanceConfig() {
    super(EurekaRegistryClient.NAMESPACE);
  }

  public String getVipAddress() {

    return configInstance.getStringProperty(EurekaRegistryClient.VIP_ADDRESS, super.getIpAddress())
        .get();
  }

  public boolean isEnabled() {

    return configInstance.getBooleanProperty(EurekaRegistryClient.ENABLED, true).get();
  }

  @Override
  public String getHostName(boolean refresh) {

    return configInstance.getBooleanProperty(EurekaRegistryClient.PREFER_IP_ADDRESS, Boolean.FALSE)
        .get() ? getIpAddress() : super.getHostName(refresh);
  }
}
