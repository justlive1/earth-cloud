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
package vip.justlive.cloud.security.support.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * security配置
 * 
 * @author wubo
 *
 */
@Configuration
@ConditionalOnProperty(name = "spring.boot.auth.enabled", havingValue = "true")
@Import({SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class SecurityConf extends WebSecurityConfigurerAdapter {

  @Value("${server.contextPath:}")
  private String contextPath;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Page with login form is served as /login.html and does a POST on /login
    http.formLogin().loginPage(contextPath + "/ui/login.html")
        .loginProcessingUrl(contextPath + "/ui/login").permitAll();
    // The UI does a POST on /logout on logout
    http.logout().logoutUrl(contextPath + "/ui/logout");
    // The ui currently doesn't support csrf
    http.csrf().disable();

    // Requests for the login page and the static assets are allowed
    http.authorizeRequests().antMatchers("/**/ui/**", "/**/*.css").permitAll();
    // ... and any other request needs to be authorized
    http.authorizeRequests().antMatchers("/**").authenticated();

    // Enable so that the clients can authenticate via HTTP basic for registering
    http.httpBasic();
  }

  @Configuration
  public class WebConf extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler(contextPath + "/ui/**")
          .addResourceLocations("classpath:/META-INF/spring-boot-admin-server-ui/")
          .resourceChain(true);
    }
  }
}
