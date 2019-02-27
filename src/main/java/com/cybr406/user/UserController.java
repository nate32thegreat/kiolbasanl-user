package com.cybr406.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    @InitBinder
    void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(new RegistrationValidator());
    }

    @Autowired
    JdbcUserDetailsManager userDetailsManager;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    User.UserBuilder userBuilder;

    @PostMapping("/signup")
    public ResponseEntity<Profile> signUp(@Valid @RequestBody Registration reg) {
        userDetailsManager.createUser(userBuilder
                .username(reg.getEmail())
                .password(reg.getPassword())
                .roles("BLOGGER")
                .build());

        Profile profile = new Profile();
        profile.setFirstName(reg.getFirstName());
        profile.setLastName(reg.getLastName());
        profile.setEmail(reg.getEmail());

       return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.CREATED);
    }

}
