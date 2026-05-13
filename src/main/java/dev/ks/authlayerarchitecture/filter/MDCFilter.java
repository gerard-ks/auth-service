package dev.ks.authlayerarchitecture.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class MDCFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String CORRELATION_ID_HEADER  = "X-Correlation-Id";
    public static final String REQUEST_METHOD_MDC_KEY = "httpMethod";
    public static final String REQUEST_URI_MDC_KEY    = "httpUri";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = resolveCorrelationId(request);

        try {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            MDC.put(REQUEST_METHOD_MDC_KEY, request.getMethod());
            MDC.put(REQUEST_URI_MDC_KEY,    request.getRequestURI());

            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            log.debug(
                    "Request started [{} {}] [correlationId={}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId
            );

            filterChain.doFilter(request, response);

            log.debug(
                    "Request completed [{} {}] [correlationId={}] [status={}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId,
                    response.getStatus()
            );

        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
            MDC.remove(REQUEST_METHOD_MDC_KEY);
            MDC.remove(REQUEST_URI_MDC_KEY);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String headerValue = request.getHeader(CORRELATION_ID_HEADER);

        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue;
        }

        return UUID.randomUUID().toString();
    }
}
