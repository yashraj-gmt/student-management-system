package com.app.security;

import com.app.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider()) // Use custom authentication provider
                .authorizeRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/forgot-password", "/verify-otp", "/reset-password/**", "/css/**").permitAll()  // Allow unauthenticated access
                        .anyRequest().authenticated() // Require authentication for all other URLs
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()  // Custom login page
                        .defaultSuccessUrl("/students", true) // Redirect to /students after successful login
                        .failureUrl("/login?error") // Redirect on failure
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Logout URL
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Bean to handle password encoding
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService((UserDetailsService) userDetailsService); // Use custom user details service
        provider.setPasswordEncoder(passwordEncoder()); // Use BCrypt password encoder
        return provider;
    }
}
