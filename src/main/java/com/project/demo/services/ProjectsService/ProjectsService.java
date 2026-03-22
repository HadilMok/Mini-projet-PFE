package com.project.demo.services.ProjectsService;


import com.project.demo.models.ProjectDTO;
import com.project.demo.models.Projects;
import com.project.demo.repositories.ProfessorRepository.ProfessorRepository;
import com.project.demo.repositories.ProjectsRepository.ProjectsRepository;
import com.project.demo.repositories.StudentRepository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectsService {
    private final ProjectsRepository repository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;


    @Transactional
    public Projects saveSingle(ProjectDTO dto) {
        // 1) Validate unique code
        if (repository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Project code already exists: " + dto.getCode());
        }

        // 2) Load required encadrant
        var enc = professorRepository.findByEmail(dto.getEncadrantEmail())
                .orElseThrow(() -> new IllegalArgumentException("Encadrant not found: " + dto.getEncadrantEmail()));

        // 3) Load optional rapporteur & president
        var rap = dto.getRapporteurEmail() == null
                ? null
                : professorRepository.findByEmail(dto.getRapporteurEmail())
                .orElseThrow(() -> new IllegalArgumentException("Rapporteur not found: " + dto.getRapporteurEmail()));

        var pres = dto.getPresidentEmail() == null
                ? null
                : professorRepository.findByEmail(dto.getPresidentEmail())
                .orElseThrow(() -> new IllegalArgumentException("President not found: " + dto.getPresidentEmail()));

        // 4) Load students (ensures list size ≥ 1 by DTO validation)
        var students = dto.getStudentEmails().stream()
                .map(id -> studentRepository.findByEmail(id)
                        .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id)))
                .toList();

        // 5) Build and save
        Projects p = Projects.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .note(dto.getNote())
                .encadrant(enc)
                .rapporteur(rap)
                .president(pres)
                .students(students)
                .build();

       Projects projects= repository.save(p);
        return projects;
    }


    public Projects createOrUpdateProjects(Projects projects) {
        return repository.save(projects);
    }

    public List<Projects> getAllProjects() {
        return repository.findAll();
    }

    public Projects getProjects(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Projects Not Found"));
    }

    public void deleteProjects(Long id) {
        repository.deleteById(id);
    }

    public void assignStudentToProject(long projectId, long studentId) {
        var project = getProjects(projectId);
        var student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student Not Found"));
        if (project.getStudents().size() >= 2) {
            throw new IllegalStateException("Project already has 2 students");
        }
        if (project.getStudents().contains(student)) {
            throw new IllegalStateException("Student already assigned to this project");
        }
        project.getStudents().add(student);
        repository.save(project);
    }

    public void assignProfessorToProject(long projectId, long encadrantId, String role) {
        var project = getProjects(projectId);
        var encadrant = professorRepository.findById(encadrantId).orElseThrow(() -> new EntityNotFoundException("Professor Not Found"));
        switch (role) {
            case "ENCADRANT":
                project.setEncadrant(encadrant);
                break;
            case "RAPPORTEUR":
                project.setRapporteur(encadrant);
                break;
            case "PRESIDENT":
                project.setPresident(encadrant);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
        }
        repository.save(project);
    }

}