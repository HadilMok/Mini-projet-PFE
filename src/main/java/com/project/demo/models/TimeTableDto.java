package com.project.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableDto {
    ConfigurationModel configurationModel;
    List<Classroom> classrooms;
    ProjectDTO projectDTO;
}
