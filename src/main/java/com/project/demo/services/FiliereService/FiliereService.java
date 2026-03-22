package com.project.demo.services.FiliereService;

import com.project.demo.exceptions.ResourceNotFoundException;
import com.project.demo.models.Filiere;
import com.project.demo.repositories.FiliereRepository.FiliereRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FiliereService {
    private final FiliereRepository repo;

    public FiliereService(FiliereRepository repo) {
        this.repo = repo;
    }

    public List<Filiere> findAll() {
        return repo.findAll();
    }

    public Filiere findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + id));
    }

    @Transactional
    public Filiere create(Filiere f) {
        if (repo.existsByCode(f.getCode())) {
            throw new IllegalArgumentException("Filiere code already exists: " + f.getCode());
        }
        return repo.save(f);
    }

    @Transactional
    public Filiere update(Long id, Filiere incoming) {
        Filiere existing = findById(id);

        // if code changed, ensure uniqueness
        if (!existing.getCode().equals(incoming.getCode())
                && repo.existsByCode(incoming.getCode())) {
            throw new IllegalArgumentException("Another Filiere already uses code: " + incoming.getCode());
        }

        existing.setName(incoming.getName());
        existing.setCode(incoming.getCode());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Filiere f = findById(id);
        repo.delete(f);
    }
}
