package com.example.secureapp.config;

import com.example.secureapp.model.AppUser;
import com.example.secureapp.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<AppUser> appUser = userRepository.findByUsername(username);
            if (appUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            AppUser u = appUser.get();
            return User.withUsername(u.getUsername())
                    .password(u.getPasswordHash())
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Require HTTPS for all requests
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())

            // Headers: HSTS, Frame options, XSS protection (modern style)
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> 
                    hsts.includeSubDomains(true)
                        .preload(true)
                        .maxAgeInSeconds(31536000)
                )
                .frameOptions(frame -> frame.deny())
                .contentSecurityPolicy(csp -> 
                    csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'")
                )
            )

            // CSRF protection enabled (default)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/**"))

            // Authentication rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/actuator/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )

            // Form login
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler((req, res, auth) -> res.setStatus(200))
                .failureHandler((req, res, ex) -> res.sendError(401, "Authentication failed"))
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    // Expose AuthenticationManager for manual auth if needed
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}