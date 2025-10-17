package com.swms.config;

import com.swms.security.JwtAuthenticationEntryPoint;
import com.swms.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - authentication
                        .requestMatchers("/api/auth/register/citizen").permitAll()
                        .requestMatchers("/api/auth/register/city-authority").permitAll()
                        .requestMatchers("/api/auth/register/driver").permitAll()
                        .requestMatchers("/api/auth/register/waste-collection-staff").permitAll()
                        .requestMatchers("/api/auth/register/sensor-manager").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/validate").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public endpoints - user management (for testing)
                        .requestMatchers("/api/citizens/**").permitAll()
                        .requestMatchers("/api/city-authorities/**").permitAll()
                        .requestMatchers("/api/drivers/**").permitAll()
                        .requestMatchers("/api/waste-collection-staff/**").permitAll()
                        .requestMatchers("/api/sensor-managers/**").permitAll()

                        // Citizen waste disposal endpoints - require authentication
                        .requestMatchers("/api/citizen/waste-disposal-requests/**").authenticated()
                        .requestMatchers("/api/citizen/**").authenticated()

                        // All other endpoints require authentication
                        .requestMatchers("/api/routes/preview").permitAll()
                        .requestMatchers("/api/routes/create/**").permitAll()
                        .requestMatchers("/api/routes/create").permitAll()
                        .requestMatchers("/api/routes/assigned").permitAll()

                        .requestMatchers("/api/routes/assigned/driver").authenticated()
                        .requestMatchers("/api/routes/driver/details").authenticated()
                        .requestMatchers("/api/routes/stops/by-ids").authenticated()
                        .requestMatchers("/api/routes/driver/**").authenticated()
                        .requestMatchers("/api/v1/smartbin/*/collect").authenticated()
                        .requestMatchers("/api/routes/stops/*/complete").authenticated()

                        .requestMatchers("/api/v1/smartbin/**").permitAll()
                        .requestMatchers("/api/v1/bin-sensors/**").permitAll()
                        .requestMatchers("/api/v1/alerts/**").permitAll()

                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use specific origins instead of patterns for better security
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}