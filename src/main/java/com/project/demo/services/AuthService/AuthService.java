package com.project.demo.services.AuthService;

import com.project.demo.configuration.JwtService;
import com.project.demo.controllers.AuthController.AuthenticationRequest;
import com.project.demo.controllers.AuthController.AuthenticationResponse;
import com.project.demo.controllers.AuthController.RegisterRequest;
import com.project.demo.models.Enumerations.Role;
import com.project.demo.models.UserAccount;
import com.project.demo.repositories.UserRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        var user = (UserAccount) auth.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        String token = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        UserAccount user = new UserAccount();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.valueOf(request.getRole() != null ? request.getRole().toUpperCase() : "ADMIN"));
        userRepository.save(user);
        Map<String, Object> claims = new HashMap<>();
        String token = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(token).build();
    }
}
