package com.cybr406.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class UserEventHandler {
    Logger logger = LoggerFactory.getLogger(UserEventHandler.class);

    @HandleBeforeSave
    @PreAuthorize("hasAnyRole('ROLE_BLOGGER','ROLE_ADMIN') or #profile.email == authentication.principal.username")
    public void handleBeforeSave(Profile profile) {
        System.out.println("Save a profile");
    }

//    @Override
//    @PreAuthorize("hasRole('ROLE_ADMIN') or #profile.email == authentication.principal.username")
//    <S extends Profile> S save(@Param("author") S entity);

    @HandleAfterCreate
    public void handleAuthorCreated(Profile profile) {
        logger.info("Profile {} created.", profile.getEmail());
    }
}
