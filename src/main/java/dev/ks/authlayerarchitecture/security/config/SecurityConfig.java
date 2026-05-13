package dev.ks.authlayerarchitecture.security.config;

import dev.ks.authlayerarchitecture.security.jwt.JwtToAuthenticationConverter;
import dev.ks.authlayerarchitecture.security.web.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtToAuthenticationConverter jwtConverter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtToAuthenticationConverter jwtConverter, JwtAuthenticationEntryPoint entryPoint, CorsConfigurationSource corsConfigurationSource) {
        this.jwtConverter = jwtConverter;
        this.entryPoint = entryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // CSRF désactivé (JWT stateless)
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .logout(AbstractHttpConfigurer::disable)

                .requestCache(AbstractHttpConfigurer::disable)

                .securityContext(AbstractHttpConfigurer::disable)

                .rememberMe(AbstractHttpConfigurer::disable)

                .anonymous(AbstractHttpConfigurer::disable)

                // CORS
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource
                ))

                // Session stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Entry point 401
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                )

                // Autorisations
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/verify-email/token",
                                "/api/auth/verify-email/otp",
                                "/api/auth/resend-verification",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/test").permitAll() // Test

                        .requestMatchers("/rqueue/**").permitAll() // Rqueue

                        // Swagger + OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Actuator health
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // Admin
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Authentifié
                        .anyRequest().authenticated()
                )

                // OAuth2 Resource Server JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
