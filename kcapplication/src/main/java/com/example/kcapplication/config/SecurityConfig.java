package com.example.kcapplication.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final JwtDecoder jwtDecoder;
    private static final String[] WHITE_LIST_URL = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private static final String[] PROTECTED_LIST_URL = {
            "/api/v1/**",
    };

    @Bean
    public SecurityFilterChain infonetSecurityFilterChain(HttpSecurity http) throws Exception {
        return createSecurityFilterChain(http);
    }

    private SecurityFilterChain createSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(WHITE_LIST_URL)
                            .permitAll();
                    req.requestMatchers(PROTECTED_LIST_URL)
                            .authenticated();
                });
        exceptionHandler(http);

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .oauth2ResourceServer(jwt -> jwt
                        .accessDeniedHandler((req, res, e) -> handleException(res, e, HttpServletResponse.SC_UNAUTHORIZED))
                        .authenticationEntryPoint((req, res, e) -> handleException(res, e, HttpServletResponse.SC_FORBIDDEN))
                        .jwt(jwt1 -> jwt1
                                .jwtAuthenticationConverter(jwtAuthConverter))
                );

        return http.build();
    }

    private void exceptionHandler(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(handle -> handle
                        .accessDeniedHandler((req, res, e) -> handleException(res, e, HttpServletResponse.SC_UNAUTHORIZED))
                        .authenticationEntryPoint((req, res, e) -> handleException(res, e, HttpServletResponse.SC_FORBIDDEN))
                );
    }

    private void handleException(HttpServletResponse res, Exception e, Integer status) throws IOException {
        String message = e.getMessage();
        if (e instanceof InvalidBearerTokenException) {
            if (e.getMessage().contains("Invalid signature")) {
                message = "Invalid Jwt Token";
            } else if (e.getMessage().contains("expired")) {
                message = "Token has expired";
                status = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            }
        }
        res.setContentType("application/json");
        res.setStatus(status);
        CustomResponse customResponse = new CustomResponse(null, message, status);
        String jsonResponse = new ObjectMapper().writeValueAsString(customResponse);
        PrintWriter writer = res.getWriter();
        writer.println(jsonResponse);
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CustomResponse {
    private Object data;
    private String message;
    private Integer status;
}
