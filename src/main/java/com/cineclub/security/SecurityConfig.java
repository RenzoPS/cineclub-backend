package com.cineclub.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Swagger UI y OpenAPI docs - acceso público
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Endpoint público de registro
                .requestMatchers("/api/auth/register").permitAll()
                
                // POST /search permitido para usuarios autenticados
                .requestMatchers(HttpMethod.POST, "/api/movies/search").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/screenings/search").hasAnyRole("USER", "ADMIN")
                
                // POST, PUT, PATCH, DELETE solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                
                // GETS solo para ADMIN
                .requestMatchers(HttpMethod.GET, "/api/rooms/**").hasRole("ADMIN")
                
                // GETS para usuarios autenticados (USER o ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/movies/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/screenings/**").hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
            
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
}
