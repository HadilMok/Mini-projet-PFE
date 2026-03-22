// src/main/java/com/project/demo/dto/ProjectDTO.java
package com.project.demo.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String note;

    @NotNull
    private String encadrantEmail;

    private String rapporteurEmail;
    private String presidentEmail;

    @NotNull @Size(min = 1)
    private List<String> studentEmails;
}
