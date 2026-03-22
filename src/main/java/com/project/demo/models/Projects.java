package com.project.demo.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Projects {
    @Id
    @GeneratedValue
    long id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false,unique = true)
    String code;
    String note;

    @OneToMany(mappedBy = "project")
    List<Student> students;

    @JoinColumn(nullable = false)
    @ManyToOne
    Professor encadrant;

    @ManyToOne
    Professor rapporteur;

    @ManyToOne
    Professor president;

}
