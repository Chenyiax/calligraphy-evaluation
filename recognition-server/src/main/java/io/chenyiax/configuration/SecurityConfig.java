package io.chenyiax.configuration;

import io.chenyiax.entity.RestBean;
import io.chenyiax.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This configuration class is used to enable and configure Spring Security for the application.
 * It defines beans for password encoding, security filter chains, and CORS configuration.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Creates a BCrypt password encoder bean with a strength factor of 10.
     * The strength factor determines the computational cost of hashing passwords,
     * which affects the security and performance of password hashing.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configures the security filter chain for the application.
     * This method sets up various security features such as CSRF protection, CORS,
     * request authorization, exception handling, session management, and JWT authentication.
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @return A SecurityFilterChain instance representing the configured security filter chain.
     * @throws Exception If an error occurs during the configuration process.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // Disable CSRF protection since the application is stateless and uses JWT
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS using the provided corsConfigurationSource bean
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configure request authorization rules
                .authorizeHttpRequests(conf -> {
                    // Permit all requests to the specified endpoints
                    conf.requestMatchers("/api/auth/*").permitAll()
                            // Require authentication for all other requests
                            .anyRequest().authenticated();
                })
                // Configure exception handling
                .exceptionHandling(conf -> {
                    // Set the handler for access denied exceptions
                    conf.accessDeniedHandler(this::handleProcess);
                    // Set the handler for authentication exceptions
                    conf.authenticationEntryPoint(this::handleProcess);
                })
                // Configure session management
                .sessionManagement(conf -> {
                    // Set the session creation policy to stateless
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Handles authentication and access denied exceptions.
     * This method writes a JSON response containing the error information to the client.
     *
     * @param request                   The HTTP request object.
     * @param response                  The HTTP response object.
     * @param exceptionOrAuthentication The exception or authentication object.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private void handleProcess(HttpServletRequest request,
                               HttpServletResponse response,
                               Object exceptionOrAuthentication) throws IOException {
        // Set the response content type to JSON with UTF-8 encoding
        response.setContentType("application/json;charset=utf-8");
        // Get the writer to write the response content
        PrintWriter writer = response.getWriter();

        // Handle access denied exceptions
        if (exceptionOrAuthentication instanceof AccessDeniedException exception) {
            writer.write(RestBean.failure(403, exception.getMessage()).asJsonString());
        }
        // Handle authentication exceptions
        else if (exceptionOrAuthentication instanceof AuthenticationException exception) {
            writer.write(RestBean.failure(401, exception.getMessage()).asJsonString());
        }
    }

    //    @Bean

    /**
     * Creates a CORS configuration source bean.
     * This method configures CORS settings to allow requests from a specific origin,
     * with any headers and methods, and supports credentials.
     *
     * @return A CorsConfigurationSource instance representing the CORS configuration.
     */
    public CorsConfigurationSource corsConfigurationSource() {
        // Create a new CORS configuration object
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Allow credentials to be included in CORS requests
        corsConfig.setAllowCredentials(true);
        // Add an allowed origin
        corsConfig.addAllowedOrigin("http://localhost:3000");
        // Allow all headers in CORS requests
        corsConfig.addAllowedHeader("*");
        // Allow all HTTP methods in CORS requests
        corsConfig.addAllowedMethod("*");

        // Create a URL-based CORS configuration source
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register the CORS configuration for all endpoints
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}