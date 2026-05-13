package dev.ks.authlayerarchitecture.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final String title;

    protected BusinessException(String errorCode, String title, String detail) {
        super(detail);
        this.errorCode = errorCode;
        this.title = title;
    }

    protected BusinessException(
            String errorCode,
            String title,
            String detail,
            Throwable cause
    ) {
        super(detail, cause);
        this.errorCode = errorCode;
        this.title     = title;
    }
}
