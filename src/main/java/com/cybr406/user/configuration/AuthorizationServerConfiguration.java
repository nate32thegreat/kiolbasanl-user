package com.cybr406.user.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

//    @Autowired
//    private UserApprovalHandler userApprovalHandler;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("api")
                    .resourceIds("profile", "post")
                    .authorizedGrantTypes("password", "refresh_token")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read", "write", "trust")
                    .secret(passwordEncoder.encode(""))
                .and()
                .withClient("post")
                    .authorities("ROLE_POST_SERVICE")
                    .secret(passwordEncoder.encode("secret"));

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager);

    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .checkTokenAccess("hasRole('ROLE_POST_SERVICE')");
    }
}
