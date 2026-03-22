package com.project.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.*;


@Configuration
public class BeansConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // <-- replace allowedOrigins("*") with allowedOriginPatterns("*")
        config.setAllowedOriginPatterns(Collections.singletonList("*"));

        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        // (optional) expose headers back to the browser
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }



}
