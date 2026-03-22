package com.project.demo.services.TimeTableService;

import com.project.demo.models.*;
import com.project.demo.services.ClassroomService.ClassroomService;
import com.project.demo.services.ConfigurationService.ConfigurationService;
import com.project.demo.services.ProjectsService.ProjectsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {
    private final ProjectsService projectsService;
    private final ConfigurationService configurationService;
    private final RestTemplate restTemplate;
    private final ClassroomService classroomService;

    @Value("${application.assignUrl}")
    String assignUrl;

    public List<TimeTableDto> getTimeTable() {
        List<Projects> projects = projectsService.getAllProjects();
        ConfigurationModel config = configurationService.findAll().getFirst();
        List<Classroom> classrooms = classroomService.findAll();

        List<TimeTableDto> timeTable = projects.stream()
                .map(project -> {
                    List<String> studentsEmails = project.getStudents()
                            .stream()
                            .map(student -> student.getEmail())
                            .toList();
                    ProjectDTO projectDTO = new ProjectDTO();
                    projectDTO.setName(project.getName());
                    projectDTO.setEncadrantEmail(project.getEncadrant().getEmail());
                    projectDTO.setCode(project.getCode());
                    projectDTO.setStudentEmails(studentsEmails);

                    return new TimeTableDto(config, classrooms, projectDTO);
                })
                .toList();

        HttpEntity<List<TimeTableDto>> requestEntity = new HttpEntity<>(timeTable);

        ResponseEntity<List<TimeTableDto>> response = restTemplate.exchange(
                assignUrl + "/generate-schedule",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<TimeTableDto>>() {
                }
        );

        return response.getBody();
    }
}
