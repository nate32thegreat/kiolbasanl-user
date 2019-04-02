package com.cybr406.user.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
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
//                .regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
//                .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
//                .regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
//                .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
//                .regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
//                .access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
        // @formatter:on

    }
}
