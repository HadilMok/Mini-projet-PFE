package com.project.demo.controllers.TimeTableController;

import com.project.demo.models.ConfigurationModel;
import com.project.demo.models.ProjectDTO;
import com.project.demo.models.Projects;
import com.project.demo.models.TimeTableDto;
import com.project.demo.services.ConfigurationService.ConfigurationService;
import com.project.demo.services.ProjectsService.ProjectsService;
import com.project.demo.services.StudentService.StudentService;
import com.project.demo.services.TimeTableService.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping
    public ResponseEntity<?> getTimeTable() {

        timeTableService.getTimeTable();


        return null;
    }
}
