package com.cybr406.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    JdbcUserDetailsManager userDetailsManager;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    User.UserBuilder userBuilder;

    @PostMapping("/signup")
    public ResponseEntity<Profile> signUp(@Valid @RequestBody Registration reg) {
        userDetailsManager.createUser(userBuilder
                .username("test@example.com")
                .password("test")
                .roles("BLOGGER")
                .build());

        Profile profile = new Profile();
        profile.setFirstName("Test");
        profile.setLastName("Testerton");
        profile.setEmail("test@example.com");

       return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.CREATED);
    }

}
