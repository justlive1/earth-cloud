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
package vip.justlive.cloud.registry.client;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;

/**
 * 服务注册客户端抽象类
 * 
 * @author wubo
 *
 */
public abstract class BaseRegistryClient implements RegistryClient {

  private static final int NON_REGISTED = 0;
  private static final int REGISTED = -1;
  private static final int STARTED = -2;
  private static final int STOPED = -3;

  /**
   * 使用了@EnableDiscoveryClient会自动配置
   */
  @Autowired(required = false)
  protected DiscoveryClient client;

  @Autowired(required = false)
  protected RegistryHandler handler;

  protected AtomicInteger autoRegister = new AtomicInteger(NON_REGISTED);

  /**
   * 非springcloud项目client为null<br>
   * springcloud项目自动注册服务中心client不为null<br>
   * 默认排除SimpleDiscoveryClient客户端<br>
   * 特殊处理@Override此方法
   * 
   * @return
   */
  protected boolean needRegister() {

    if (handler != null) {
      return handler.needRegister();
    }

    if (client == null || SimpleDiscoveryClient.class.isInstance(client)) {
      return true;
    }

    if (CompositeDiscoveryClient.class.isInstance(client)) {

      List<DiscoveryClient> clients = ((CompositeDiscoveryClient) client).getDiscoveryClients();
      if (clients == null || clients.isEmpty()) {
        return true;
      }

      int count = 0;
      for (DiscoveryClient c : clients) {
        if (!SimpleDiscoveryClient.class.isInstance(c)) {
          count++;
        }
      }
      return count == 0;
    }

    return false;
  }

  /**
   * 注册服务处理
   */
  protected abstract void doRegister();

  @PostConstruct
  @Override
  public void register() {
    if (needRegister()) {
      if (autoRegister.compareAndSet(NON_REGISTED, REGISTED)) {
        doRegister();
      }
    } else {
      autoRegister.compareAndSet(NON_REGISTED, REGISTED);
    }
  }

  /**
   * 运行服务
   */
  protected abstract void doStart();

  @Override
  public void start() {

    if (autoRegister.compareAndSet(REGISTED, STARTED)) {
      doStart();
    }
  }

  /**
   * 停止服务
   */
  protected abstract void doShutdown();

  @PreDestroy
  @Override
  public void shutdown() {

    if (autoRegister.compareAndSet(STARTED, STOPED)) {
      doShutdown();
    }
  }
}
