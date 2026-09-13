package com.flowb2b.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.flowb2b.auth.jwt.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // =========================
            // CORS
            // =========================
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()
                )
            )

            // =========================
            // CSRF
            // =========================
            .csrf(csrf ->
                csrf.disable()
            )

            // =========================
            // JWT = SIN SESIONES
            // =========================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // =========================
            // ERRORES
            // =========================
            .exceptionHandling(exception -> exception

                .authenticationEntryPoint(
                    (request, response, authException) -> {

                        response.setStatus(401);

                        response.setContentType(
                            MediaType.APPLICATION_JSON_VALUE
                        );

                        try {

                            response.getWriter().write(
                                """
                                {
                                  "estado": 401,
                                  "error": "No autorizado",
                                  "mensaje": "Debes iniciar sesión y enviar un token JWT válido"
                                }
                                """
                            );

                        } catch (IOException e) {

                            throw new RuntimeException(e);
                        }
                    }
                )

                .accessDeniedHandler(
                    (request, response, accessDeniedException) -> {

                        response.setStatus(403);

                        response.setContentType(
                            MediaType.APPLICATION_JSON_VALUE
                        );

                        try {

                            response.getWriter().write(
                                """
                                {
                                  "estado": 403,
                                  "error": "Acceso denegado",
                                  "mensaje": "No tienes permisos para realizar esta acción"
                                }
                                """
                            );

                        } catch (IOException e) {

                            throw new RuntimeException(e);
                        }
                    }
                )
            )

            // =========================
            // ENDPOINTS
            // =========================
            .authorizeHttpRequests(auth -> auth

                // =========================
                // LOGIN PÚBLICO
                // =========================
                .requestMatchers(
                    "/api/auth/login"
                )
                .permitAll()

                // =========================
                // SWAGGER / OPENAPI PÚBLICO
                // =========================
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                )
                .permitAll()

                // =========================
                // TODO LO DEMÁS REQUIERE JWT
                // =========================
                .anyRequest()
                .authenticated()
            )

            // =========================
            // FILTRO JWT
            // =========================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =========================
    // CONFIGURACIÓN CORS
    // =========================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "Accept"
            )
        );

        configuration.setExposedHeaders(
            List.of(
                "Authorization"
            )
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }

    // =========================
    // PASSWORD ENCODER
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}