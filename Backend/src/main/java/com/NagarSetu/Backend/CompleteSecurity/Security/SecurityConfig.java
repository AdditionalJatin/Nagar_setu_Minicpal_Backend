package com.NagarSetu.Backend.CompleteSecurity.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Allows you to use @PreAuthorize on specific controller methods!
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery)
                // Safe to disable because we are using stateless JWTs, not browser cookies
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Enable CORS (Cross-Origin Resource Sharing) for your Flutter app / Web dashboard
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Define Endpoint Access Rules based on your UserRole Enum
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: Login and Register need to be accessible by anyone
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin endpoints: Only MASTER_ADMIN or CITY_ADMIN can access
                        .requestMatchers("/api/admin/**").hasAnyRole("MASTER_ADMIN", "CITY_ADMIN")

                        // Ward-specific endpoints: WARD_HEAD and above
                        .requestMatchers("/api/ward/**").hasAnyRole("WARD_HEAD", "CITY_ADMIN", "MASTER_ADMIN")

                        // Worker tasks: Only WORKER or Admins
                        .requestMatchers("/api/tasks/**").hasAnyRole("WORKER", "WARD_HEAD")

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Everything else requires the user to be at least logged in (CITIZEN included)
                        .anyRequest().authenticated()
                )

                // 4. Tell Spring Security not to store session state (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Hook up the Authentication Provider (handles password checking)
                .authenticationProvider(authenticationProvider)

                // 6. Insert your custom JWT filter BEFORE Spring's default password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS configuration so your frontend can communicate with this backend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Change to your actual frontend URL in production
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
