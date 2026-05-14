package com.project.demo.controllers.AuthController;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String email;
    String password;
    String firstName;
    String lastName;
    String role;
}
