package com.project.demo.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class PasswordUpdater {
    String oldPassword;
    String newPassword;
}