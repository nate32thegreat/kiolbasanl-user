package com.cybr406.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RegistrationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Registration.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "email",
                "field.required",
                "Email is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "password",
                "field.required",
                "Password is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "firstName",
                "field.required",
                "First Name is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "lastName",
                "field.required",
                "Last Name is a required field.");

    }
}


