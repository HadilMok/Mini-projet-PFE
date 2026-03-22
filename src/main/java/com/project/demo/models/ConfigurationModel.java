package com.project.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ConfigurationModel {
    @Id
    @GeneratedValue
    long id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    boolean status;
}
