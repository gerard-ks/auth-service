package dev.ks.authlayerarchitecture.service.password;

public interface PasswordService {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

    void assertNotSamePassword(
            String rawNewPassword,
            String encodedOldPassword
    );
}
