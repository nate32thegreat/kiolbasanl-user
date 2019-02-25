package com.cybr406.user.configuration;

import com.cybr406.user.ProfileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class UserConfiguration implements RepositoryRestConfigurer {

    @Autowired
    ProfileValidator profileValidator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", profileValidator);
        validatingListener.addValidator("beforeSave", profileValidator);

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

    }
}
