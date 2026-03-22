package com.project.demo.models;

import com.project.demo.models.Enumerations.Seance;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ProjectResult {
    @Id
    @GeneratedValue
    long id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String code;
    @Column(nullable = false)
    String note;
    @Column(nullable = false)
    LocalDate date;

    @JoinColumn(nullable = false)
    @ManyToOne
    Classroom room;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Seance seance;

//    @JoinColumn
//    @OneToMany
//    List<Student> students;


    @JoinColumn(nullable = false)
    @ManyToOne
    Professor encadrant;

    @JoinColumn(nullable = false)
    @ManyToOne
    Professor rapporteur;

    @JoinColumn(nullable = false)
    @ManyToOne
    Professor president;

}
