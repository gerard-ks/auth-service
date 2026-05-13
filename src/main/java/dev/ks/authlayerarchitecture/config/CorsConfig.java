package dev.ks.authlayerarchitecture.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
public class CorsConfig {

    @Value("${auth-service.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${auth-service.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${auth-service.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${auth-service.cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${auth-service.cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${auth-service.cors.max-age}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                Arrays.asList(allowedOrigins.split(","))
        );

        config.setAllowedMethods(
                Arrays.asList(allowedMethods.split(","))
        );

        config.setAllowedHeaders(
                Arrays.asList(allowedHeaders.split(","))
        );

        config.setExposedHeaders(
                Arrays.asList(exposedHeaders.split(","))
        );

        config.setAllowCredentials(allowCredentials);
        config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
