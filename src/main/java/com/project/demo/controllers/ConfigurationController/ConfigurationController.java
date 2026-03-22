package com.project.demo.controllers.ConfigurationController;

import com.project.demo.models.ConfigurationModel;
import com.project.demo.models.Filiere;

import com.project.demo.services.ConfigurationService.ConfigurationService;
import com.project.demo.services.FiliereService.FiliereService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/configuration")
@Validated
public class ConfigurationController {

    private final ConfigurationService service;
    public ConfigurationController(ConfigurationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConfigurationModel> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ConfigurationModel getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ConfigurationModel> create(@Valid @RequestBody ConfigurationModel f) {
        ConfigurationModel created = service.create(f);
        // you could build URI pointing to /api/filieres/{created.getId()}
        return ResponseEntity
                .created(URI.create("/configuration/" + created.getId()))
                .body(created);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.reset(id);
        return ResponseEntity.noContent().build();
    }
}
