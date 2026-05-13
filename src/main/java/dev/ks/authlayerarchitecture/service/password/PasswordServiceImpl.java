package dev.ks.authlayerarchitecture.service.password;

import dev.ks.authlayerarchitecture.exception.password.SamePasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void assertNotSamePassword(
            String rawNewPassword,
            String encodedOldPassword
    ) {
        if (passwordEncoder.matches(rawNewPassword, encodedOldPassword)) {
            throw new SamePasswordException();
        }
    }
}
