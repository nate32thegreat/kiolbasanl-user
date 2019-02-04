package com.cybr406.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ProfileValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Profile.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "firstName",
                "field.required",
                "First name is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "lastName",
                "field.required",
                "Last name is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "email",
                "field.required",
                "Email is a required field.");
    }
}
