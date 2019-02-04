package com.cybr406.user.configuration;

import com.cybr406.user.ProfileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class UserConfiguration implements RepositoryRestConfigurer {

    @Autowired
    ProfileValidator profileValidator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", profileValidator);
        validatingListener.addValidator("beforeSave", profileValidator);
    }
}
