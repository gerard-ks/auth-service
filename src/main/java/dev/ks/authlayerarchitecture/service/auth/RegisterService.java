package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.dto.request.auth.RegisterRequest;

public interface RegisterService {
    void register(RegisterRequest request);
}
