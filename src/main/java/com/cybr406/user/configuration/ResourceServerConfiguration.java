package com.cybr406.user.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("profile").stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/profiles/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/profiles/**").permitAll()
                .antMatchers("/profiles/**", "/profiles", "/posts", "/posts/**")
                .access("#oauth2.isOAuth() and hasAnyRole('ROLE_BLOGGER', 'ROLE_ADMIN')");
    }
}
