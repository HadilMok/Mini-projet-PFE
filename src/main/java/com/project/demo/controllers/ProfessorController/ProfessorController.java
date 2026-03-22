package com.project.demo.controllers.ProfessorController;

import com.project.demo.models.Enumerations.Role;
import com.project.demo.models.Professor;
import com.project.demo.services.ProfessorService.ProfessorService;
import com.project.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService service;
    private final ProfessorService professorService;

    @PostMapping
    public ResponseEntity<Professor> createProfessor(@RequestBody Professor professor) {
        professor.setPassword(Utils.generatePassword());
        professor.setPhone("");
        professor.setRole(Role.PROFESSOR);
        return ResponseEntity.ok(service.createOrUpdateProfessor(professor));
    }

    @GetMapping
    ResponseEntity<List<Professor>> getAllProfessor() {
        return ResponseEntity.ok(service.getAllProfessor());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> getProfessor(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getProfessor(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(
            @PathVariable("id") Long id
    ) {
        service.deleteProfessor(id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    ResponseEntity<Professor> updateEtudiant(@RequestBody Professor professor) {
        return ResponseEntity.ok(service.createOrUpdateProfessor(professor));
    }

    @PostMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter uploadWithProgress(@RequestBody List<Professor> professors) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        CompletableFuture.runAsync(() -> {
            try {
                int total = professors.size();
                for (int i = 0; i < total; i++) {
                    professorService.saveSingle(professors.get(i));
                    double pct = (i + 1) * 100.0 / total;

                    emitter.send(SseEmitter.event()
                            .name("progress")
                            .data(pct));
                }
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("All professors saved"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }
}
