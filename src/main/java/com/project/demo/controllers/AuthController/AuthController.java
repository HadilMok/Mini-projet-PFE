package com.project.demo.controllers.AuthController;

import com.project.demo.models.UserAccount;
import com.project.demo.repositories.UserRepository.UserRepository;
import com.project.demo.services.AuthService.AuthService;
import com.project.demo.services.MailService.EmailService;
import com.project.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userAccountRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }
    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@RequestBody AuthenticationRequest authRequest) {
         Optional<UserAccount> finder=userAccountRepository.findByEmail(authRequest.getEmail());
         if(finder.isPresent()){
             UserAccount user=finder.get();
             user.setPassword(Utils.generatePassword());
             emailService.sendSimpleEmail(user.getEmail(),"new Password","Your new password:"+user.getPassword());
             return ResponseEntity.ok("Password has been sent to your email.");
         }else{
             return ResponseEntity.badRequest().build();
         }
    }
}
