package com.project.demo.controllers.FiliereController;

import com.project.demo.models.Filiere;

import com.project.demo.services.FiliereService.FiliereService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/filieres")
@Validated
public class FiliereController {

    private final FiliereService service;
    public FiliereController(FiliereService service) {
        this.service = service;
    }

    @GetMapping
    public List<Filiere> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Filiere getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Filiere> create(@Valid @RequestBody Filiere f) {
        Filiere created = service.create(f);
        // you could build URI pointing to /api/filieres/{created.getId()}
        return ResponseEntity
                .created(URI.create("/api/filieres/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public Filiere update(@PathVariable Long id, @Valid @RequestBody Filiere f) {
        return service.update(id, f);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
