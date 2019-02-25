package com.cybr406.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.security.access.prepost.PreAuthorize;

public class UserEventHandler {
    Logger logger = LoggerFactory.getLogger(UserEventHandler.class);

    @HandleBeforeSave
    @PreAuthorize("hasRole('ROLE_BLOGGER') or #author.username == authentication.principal.username")
    public void handleBeforeSave(Profile profile) {
        System.out.println("Save an author");
    }

    @HandleAfterCreate
    public void handleAuthorCreated(Profile profile) {
        logger.info("Author {} created.", profile.getEmail());
    }
}
