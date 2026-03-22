package com.project.demo.controllers.ProjectsController;

import com.project.demo.models.ProjectDTO;
import com.project.demo.models.Projects;
import com.project.demo.services.ProjectsService.ProjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectsController {
    private final ProjectsService service;
    private final ProjectsService projectsService;


    @PostMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter uploadWithProgress(@RequestBody @Valid List<ProjectDTO> projects) {
        SseEmitter emitter = new SseEmitter(0L); // disable timeout

        CompletableFuture.runAsync(() -> {
            try {
                int total = projects.size();
                for (int i = 0; i < total; i++) {
                    projectsService.saveSingle(projects.get(i));
                    double pct = (i + 1) * 100.0 / total;
                    emitter.send(SseEmitter.event()
                            .name("progress")
                            .data(pct));
                }
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("All projects saved"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }


    @PostMapping
    public ResponseEntity<Projects> createProjects(@RequestBody ProjectDTO projects) {
        return ResponseEntity.ok(service.saveSingle(projects));
    }

    @GetMapping
    ResponseEntity<List<Projects>> getAllProjects() {
        return ResponseEntity.ok(service.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projects> getProjects(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getProjects(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjects(
            @PathVariable("id") Long id
    ) {
        service.deleteProjects(id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    ResponseEntity<Projects> updateEtudiant(@RequestBody Projects projects) {
        return ResponseEntity.ok(service.createOrUpdateProjects(projects));
    }

    @PostMapping("/assignStudent/{projectId}/{studentId}")
    public ResponseEntity<Void> assignStudentToProject(
            @PathVariable long projectId,
            @PathVariable long studentId
    ) {
        service.assignStudentToProject(projectId, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("assignProfessor/{projectId}/{professorId}")
    public ResponseEntity<Void> assignEncadrantToProject(
            @PathVariable("projectId") long projectId,
            @PathVariable("professorId") long professorId,
            @RequestBody String role
    ) {
        service.assignProfessorToProject(projectId, professorId, role.toUpperCase());
        return ResponseEntity.ok().build();
    }
}
