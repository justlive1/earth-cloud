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

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;

/**
 * 通知配置类
 * 
 * @author wubo
 *
 */
@Configuration
@ConditionalOnProperty(name = "spring.boot.admin.notify.reminder.enabled", havingValue = "true")
@ConditionalOnBean(Notifier.class)
@EnableScheduling
public class NotifierConf {

  @Autowired
  Notifier notifier;

  @Value("${spring.boot.admin.notify.reminder.periodForMinutes:5}")
  Long periodForMinutes;

  @Bean
  public FilteringNotifier filteringNotifier() {
    return new FilteringNotifier(notifier);
  }

  @Bean
  @Primary
  public RemindingNotifier remindingNotifier() {
    RemindingNotifier remindingNotifier = new RemindingNotifier(notifier);
    remindingNotifier.setReminderPeriod(TimeUnit.MINUTES.toMillis(periodForMinutes));
    return remindingNotifier;
  }

  @Scheduled(fixedRateString = "${spring.boot.admin.notify.reminder.scheduledFixedRate:60000}")
  public void remind() {
    remindingNotifier().sendReminders();
  }
}
