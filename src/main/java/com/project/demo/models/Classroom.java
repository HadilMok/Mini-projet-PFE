package com.project.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Classroom {
    @Id
    @GeneratedValue
    long id;
    @Column(nullable = false)
    Integer number;
    @Column(nullable = false)
    String blocName;
    
    boolean active=true;
}
