package com.project.demo.configuration;

import com.project.demo.models.Enumerations.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {"/api/v1/public/**", "/api/v1/test/**", "/swagger-ui/**", "/v3/api-docs*/**"};
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(req ->
                            req.requestMatchers(WHITE_LIST_URL)
                                    .permitAll()
                                    .requestMatchers("/api/v1/protected/**").hasAnyRole(Role.ADMIN.name())
                                    .requestMatchers("/api/v1/connected/**").hasAnyRole(Role.ADMIN.name(), Role.STUDENT.name(), Role.PROFESSOR.name())
                                    .requestMatchers("/api/v1/manager/**").hasAnyRole(Role.ADMIN.name(), Role.PROFESSOR.name())
                                    .requestMatchers("/api/v1/prof/**").hasAnyRole(Role.PROFESSOR.name(), Role.ADMIN.name())
                                    .requestMatchers("/api/v1/student/**").hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name())
                                    .anyRequest()
                                    .permitAll()
                                   // .authenticated()

                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(exceptions ->
                            exceptions.accessDeniedHandler(accessDeniedHandler)
                    )
            ;
            return http.build();
        } catch (Exception e) {
            throw new RuntimeException("Error configuring HTTP security", e);
        }
    }
}
