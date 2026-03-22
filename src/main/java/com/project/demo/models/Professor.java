package com.project.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
public class Professor extends UserAccount {


    String matricule;

    @ManyToOne
    Projects project;

}
