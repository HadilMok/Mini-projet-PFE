package com.project.demo.controllers.AuthController;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    @Column(length = 1024)
    String token;
}
