package com.project.demo.services.ProfessorService;


import com.project.demo.models.Professor;
import com.project.demo.repositories.ProfessorRepository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepository repository;
    private final ProfessorRepository professorRepository;

    @Transactional
    public void saveSingle(Professor dto) {
        Professor p = Professor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .matricule(dto.getMatricule())
                .project(null)
                .build();
        professorRepository.save(p);
    }

    public Professor createOrUpdateProfessor(Professor professor) {
        return repository.save(professor);
    }

    public List<Professor> getAllProfessor() {
        return repository.findAll();
    }

    public Professor getProfessor(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Professor Not Found"));
    }

    public void deleteProfessor(Long id) {
        repository.deleteById(id);
    }
}