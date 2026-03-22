package com.project.demo.services.AuthService;

import com.project.demo.configuration.JwtService;
import com.project.demo.controllers.AuthController.AuthenticationRequest;
import com.project.demo.controllers.AuthController.AuthenticationResponse;
import com.project.demo.models.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(),authRequest.getPassword())
        );

        var user = (UserAccount) auth.getPrincipal();
        Map<String,Object> claims = new HashMap<>();
        String token = jwtService.generateToken(claims,user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();

    }
}
