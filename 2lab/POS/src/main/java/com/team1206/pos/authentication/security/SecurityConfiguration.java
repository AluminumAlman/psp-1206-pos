package com.team1206.pos.authentication.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
public class SecurityConfiguration {
    private final JWTFilter filter;
    private final POSUserDetailsService uds;

    public SecurityConfiguration(JWTFilter filter, POSUserDetailsService uds) {
        this.filter = filter;
        this.uds = uds;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Allow swagger-ui and authentication to be accessed without authentication
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/auth/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                    // Allow all roles, except EMPLOYEE, to access the following user endpoints
                    .requestMatchers(HttpMethod.POST, "/users/**").hasAnyAuthority(
                            "SUPER_ADMIN",
                            "MERCHANT_OWNER",
                            "EMPLOYEE"
                    ).requestMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority(
                            "SUPER_ADMIN",
                            "MERCHANT_OWNER"
                    ).requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority(
                            "SUPER_ADMIN",
                            "MERCHANT_OWNER"
                    )

                    // Match all other requests
                    .anyRequest().authenticated())
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint((request, response, authException) ->
                        { log.info("Handling exception for {} at {}: {}", request.getMethod(), request.getRequestURL(), authException.toString()); response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); }))
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(uds);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
