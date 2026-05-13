package dev.ks.authlayerarchitecture.exception;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        HttpStatus status    = resolveStatus(ex.getErrorCode());
        String correlationId = correlationId();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(status, ex.getMessage());

        problem.setTitle(ex.getTitle());
        problem.setProperty("errorCode",     ex.getErrorCode());
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());

        log.warn(
                "Business exception [correlationId={}] [{}] {} : {}",
                correlationId,
                status.value(),
                ex.getErrorCode(),
                ex.getMessage()
        );

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String correlationId = correlationId();

        List<FieldErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorDetail)
                .toList();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Request validation failed"
                );

        problem.setTitle("Validation Failed");
        problem.setProperty("errorCode",     ErrorCodeConstants.VALIDATION_ERROR);
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());
        problem.setProperty("errors",        errors);

        log.warn(
                "Validation failed [correlationId={}] — {} error(s)",
                correlationId,
                errors.size()
        );

        return problem;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(MissingRequestHeaderException ex) {
        String correlationId = correlationId();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Required header '" + ex.getHeaderName() + "' is missing"
                );

        problem.setTitle("Missing request header");
        problem.setProperty("errorCode",     ErrorCodeConstants.VALIDATION_ERROR);
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());

        log.warn(
                "Missing header [correlationId={}] : {}",
                correlationId,
                ex.getHeaderName()
        );

        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        String correlationId = correlationId();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this resource"
                );

        problem.setTitle("Access denied");
        problem.setProperty("errorCode",     ErrorCodeConstants.ACCESS_DENIED);
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());

        log.warn(
                "Access denied [correlationId={}] : {}",
                correlationId,
                ex.getMessage()
        );

        return problem;
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ProblemDetail handleUnauthenticated(InsufficientAuthenticationException ex) {
        String correlationId = correlationId();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.UNAUTHORIZED,
                        "You must be authenticated to access this resource"
                );

        problem.setTitle("Authentication required");
        problem.setProperty("errorCode",     ErrorCodeConstants.UNAUTHENTICATED);
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());

        log.warn(
                "Unauthenticated access [correlationId={}]",
                correlationId
        );

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        String correlationId = correlationId();

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later"
                );

        problem.setTitle("Internal server error");
        problem.setProperty("errorCode",     ErrorCodeConstants.INTERNAL_SERVER_ERROR);
        problem.setProperty("correlationId", correlationId);
        problem.setProperty("timestamp",     Instant.now().toString());

        log.error(
                "Unexpected error [correlationId={}]",
                correlationId,
                ex
        );

        return problem;
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage() != null
                        ? fieldError.getDefaultMessage()
                        : "Invalid value"
        );
    }


    private String correlationId() {
        String id = MDC.get("correlationId");
        return id != null ? id : "unknown";
    }

    private HttpStatus resolveStatus(String errorCode) {
        return switch (errorCode) {

            case ErrorCodeConstants.INVALID_CREDENTIALS,
                 ErrorCodeConstants.INVALID_REFRESH_TOKEN,
                 ErrorCodeConstants.REFRESH_TOKEN_EXPIRED,
                 ErrorCodeConstants.INVALID_ACCESS_TOKEN,
                 ErrorCodeConstants.ACCESS_TOKEN_EXPIRED,
                 ErrorCodeConstants.ACCOUNT_NOT_VERIFIED,
                 ErrorCodeConstants.ACCOUNT_DISABLED,
                 ErrorCodeConstants.INVALID_VERIFICATION_TOKEN,
                 ErrorCodeConstants.VERIFICATION_TOKEN_EXPIRED,
                 ErrorCodeConstants.INVALID_OTP,
                 ErrorCodeConstants.OTP_EXPIRED,
                 ErrorCodeConstants.INVALID_RESET_TOKEN,
                 ErrorCodeConstants.RESET_TOKEN_EXPIRED
                    -> HttpStatus.UNAUTHORIZED;

            case ErrorCodeConstants.ACCOUNT_LOCKED,
                 ErrorCodeConstants.OTP_BLOCKED,
                 ErrorCodeConstants.RESEND_TOO_SOON
                    -> HttpStatus.TOO_MANY_REQUESTS;

            case ErrorCodeConstants.ACCOUNT_NOT_FOUND,
                 ErrorCodeConstants.ROLE_NOT_FOUND,
                 ErrorCodeConstants.PERMISSION_NOT_FOUND
                    -> HttpStatus.NOT_FOUND;

            case ErrorCodeConstants.EMAIL_ALREADY_EXISTS,
                 ErrorCodeConstants.ROLE_ALREADY_EXISTS,
                 ErrorCodeConstants.ROLE_ALREADY_ASSIGNED,
                 ErrorCodeConstants.PERMISSION_ALREADY_EXISTS,
                 ErrorCodeConstants.PERMISSION_ALREADY_ASSIGNED,
                 ErrorCodeConstants.ACCOUNT_ALREADY_DISABLED,
                 ErrorCodeConstants.ACCOUNT_ALREADY_ENABLED
                    -> HttpStatus.CONFLICT;

            case ErrorCodeConstants.SAME_PASSWORD,
                 ErrorCodeConstants.INVALID_CURRENT_PASSWORD,
                 ErrorCodeConstants.INVALID_PASSWORD_POLICY,
                 ErrorCodeConstants.SELF_DISABLE_NOT_ALLOWED,
                 ErrorCodeConstants.ACCOUNT_NOT_ACTIVE,
                 ErrorCodeConstants.ROLE_NOT_ASSIGNED,
                 ErrorCodeConstants.PERMISSION_NOT_ASSIGNED,
                 ErrorCodeConstants.ROLE_STILL_ASSIGNED,
                 ErrorCodeConstants.PERMISSION_STILL_ASSIGNED,
                 ErrorCodeConstants.SYSTEM_ROLE_DELETION_NOT_ALLOWED,
                 ErrorCodeConstants.SYSTEM_PERM_DELETION_NOT_ALLOWED
                    -> HttpStatus.UNPROCESSABLE_ENTITY;

            case ErrorCodeConstants.INVALID_TOKEN_STRATEGY
                    -> HttpStatus.BAD_REQUEST;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}