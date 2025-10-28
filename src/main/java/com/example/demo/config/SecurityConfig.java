package com.example.demo.config; // Use your correct package name

import com.example.demo.security.CustomAuthenticationSuccessHandler; // 1. IMPORT the new handler
import com.example.demo.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    // 2. INJECT your new custom handler
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz

                        // This part allows CSS, JS, etc. to load
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // You MUST permit access to the login page itself.
                        .requestMatchers("/login", "/register", "/access-denied").permitAll()

                        // All other pages require a login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // This line tells security WHERE your login page is
                        .loginPage("/login")
                        // 3. USE THE HANDLER instead of defaultSuccessUrl
                        .successHandler(customAuthenticationSuccessHandler)
                        // .defaultSuccessUrl("/employees", true) // <-- This is replaced
                        .permitAll() // This also helps permit the form processing
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}