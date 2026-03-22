package com.project.demo.controllers.ClassroomController;// src/main/java/com/project/demo/controller/ClassroomController.java

import com.project.demo.models.Classroom;

import com.project.demo.services.ClassroomService.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/classrooms")
@Validated
public class ClassroomController {

    private final ClassroomService service;
    public ClassroomController(ClassroomService service) {
        this.service = service;
    }

    @GetMapping
    public List<Classroom> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Classroom getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Classroom> create(@Valid @RequestBody Classroom c) {
        Classroom created = service.create(c);
        return ResponseEntity
                .created(null)    // you can build a URI to `/api/classrooms/{created.getId()}`
                .body(created);
    }

    @PutMapping("/{id}")
    public Classroom update(@PathVariable Long id, @Valid @RequestBody Classroom c) {
        return service.update(id, c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
