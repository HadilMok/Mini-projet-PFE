package com.project.demo.controllers.StudentController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    long inscriptionNumber;
    String firstName;
    String lastName;
    String email;

}
