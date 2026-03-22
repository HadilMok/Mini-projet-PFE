package com.project.demo.services.ClassroomService;


import com.project.demo.exceptions.ResourceNotFoundException;
import com.project.demo.models.Classroom;
import com.project.demo.repositories.ClassroomRepository.ClassroomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassroomService {
    private final ClassroomRepository repo;

    public ClassroomService(ClassroomRepository repo) {
        this.repo = repo;
    }

    public List<Classroom> findAll() {
        return repo.findAll();
    }

    public Classroom findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + id));
    }

    @Transactional
    public Classroom create(Classroom c) {
        if (repo.existsByNumberAndBlocName(c.getNumber(), c.getBlocName())) {
            throw new IllegalArgumentException(
                    "Classroom already exists: number=" + c.getNumber() + ", bloc=" + c.getBlocName());
        }
        return repo.save(c);
    }

    @Transactional
    public Classroom update(Long id, Classroom incoming) {
        Classroom existing = findById(id);
        // check for duplicate code/number if changed
        if (!(existing.getNumber().equals(incoming.getNumber())
                && existing.getBlocName().equals(incoming.getBlocName()))
                && repo.existsByNumberAndBlocName(incoming.getNumber(), incoming.getBlocName())) {
            throw new IllegalArgumentException(
                    "Another classroom already uses number=" + incoming.getNumber() +
                            " and bloc=" + incoming.getBlocName());
        }
        existing.setNumber(incoming.getNumber());
        existing.setBlocName(incoming.getBlocName());
        existing.setActive(incoming.isActive());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Classroom c = findById(id);
        repo.delete(c);
    }
}
