package com.example.pwctictactoebackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        httpSecurity
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/login", "/ws").permitAll()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .oauth2ResourceServer()
                .jwt();

        return httpSecurity.build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new CustomTokenResolver();
    }


}
