package com.cybr406.user.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.h2.console.enabled}")
    boolean h2ConsoleEnabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Configure security for the h2 console. This should only ever happen in dev environments.
        if (h2ConsoleEnabled) {
            http
                    .authorizeRequests()
                    .antMatchers("/h2-console", "/h2-console/**").permitAll();

            // By default, frame options is set to DENY. The h2 console is rendered in a frame, however. Changing to
            // SAMEORIGIN allows the content to appear since it is originating from the same server. DENY is a better
            // option for prod, where the h2 console should be disabled anyhow.
            // See https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
            http.headers().frameOptions().sameOrigin();
        }

        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.POST, "/signup").permitAll()
                .antMatchers(HttpMethod.GET, "/profiles", "/profiles/**").permitAll()
                .anyRequest().hasAnyRole("ADMIN", "BLOGGER")
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().authenticationEntryPoint((request, response, authException) -> {
            if (authException != null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
            }
        });


    }

    @Autowired
    DataSource dataSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    User.UserBuilder userBuilder() {
        PasswordEncoder passwordEncoder = passwordEncoder();
        User.UserBuilder users = User.builder();
        users.passwordEncoder(passwordEncoder::encode);
        return users;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();

        User.UserBuilder users = User.builder();
        users.passwordEncoder(passwordEncoder::encode);

        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser(users
                        .username("admin")
                        .password("admin")
                        .roles("ADMIN"));
//            .withUser(users
//                    .username("test@example.com")
//                    .password("test")
//                    .roles("BLOGGER"));

    }
}
