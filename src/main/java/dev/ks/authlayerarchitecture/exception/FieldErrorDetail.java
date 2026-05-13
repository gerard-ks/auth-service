package dev.ks.authlayerarchitecture.exception;

public record FieldErrorDetail(
        String field,
        String message
) {}
