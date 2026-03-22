package com.project.demo.configuration;



import com.project.demo.repositories.UserRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userAccountRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                return userAccountRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
            } catch (Exception e) {
                throw new RuntimeException("No User Found With This Data", e);
            }

        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        try {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        } catch (Exception e) {
            throw new RuntimeException("Error configuring authentication provider", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        try {
            return authenticationConfiguration.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException("Error creating authentication manager", e);
        }
    }
}
