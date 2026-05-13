package dev.ks.authlayerarchitecture.validation.validator;

import dev.ks.authlayerarchitecture.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordPolicyValidator
        implements ConstraintValidator<ValidPassword, String> {

    private static final int    MIN_LENGTH       = 8;
    private static final String UPPERCASE_REGEX  = ".*[A-Z].*";
    private static final String LOWERCASE_REGEX  = ".*[a-z].*";
    private static final String DIGIT_REGEX      = ".*[0-9].*";
    private static final String SPECIAL_REGEX    =
            ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context
    ) {
        if (password == null || password.isBlank()) {
            return true; // @NotBlank gère ce cas
        }

        boolean valid = true;

        // Désactiver le message par défaut
        context.disableDefaultConstraintViolation();

        // Longueur minimale
        if (password.length() < MIN_LENGTH) {
            context.buildConstraintViolationWithTemplate(
                    "Password must be at least " + MIN_LENGTH + " characters"
            ).addConstraintViolation();
            valid = false;
        }

        //  Majuscule
        if (!password.matches(UPPERCASE_REGEX)) {
            context.buildConstraintViolationWithTemplate(
                    "Password must contain at least one uppercase letter"
            ).addConstraintViolation();
            valid = false;
        }

        //  Minuscule
        if (!password.matches(LOWERCASE_REGEX)) {
            context.buildConstraintViolationWithTemplate(
                    "Password must contain at least one lowercase letter"
            ).addConstraintViolation();
            valid = false;
        }

        //  Chiffre
        if (!password.matches(DIGIT_REGEX)) {
            context.buildConstraintViolationWithTemplate(
                    "Password must contain at least one digit"
            ).addConstraintViolation();
            valid = false;
        }

        //  Caractère spécial
        if (!password.matches(SPECIAL_REGEX)) {
            context.buildConstraintViolationWithTemplate(
                    "Password must contain at least one special character"
            ).addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
