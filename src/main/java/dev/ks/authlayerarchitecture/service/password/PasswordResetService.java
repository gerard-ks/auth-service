package dev.ks.authlayerarchitecture.service.password;

public interface PasswordResetService {

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    void changePassword(
            java.util.UUID accountId,
            String currentPassword,
            String newPassword
    );
}
