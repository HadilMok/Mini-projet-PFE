package com.project.demo.services.ConfigurationService;

import com.project.demo.exceptions.ResourceNotFoundException;
import com.project.demo.models.ConfigurationModel;
import com.project.demo.models.Filiere;
import com.project.demo.repositories.ConfigurationRepository.ConfigurationRepository;
import com.project.demo.repositories.FiliereRepository.FiliereRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigurationService {
    private final ConfigurationRepository repo;

    public ConfigurationService(ConfigurationRepository repo) {
        this.repo = repo;
    }

    public List<ConfigurationModel> findAll() {
        return repo.findAll();
    }

    public ConfigurationModel findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration not found with id " + id));
    }

    @Transactional
    public ConfigurationModel create(ConfigurationModel f) {
        if (repo.count()>0) {
            throw new IllegalArgumentException("Configuration  already exists: ");
        }
        return repo.save(f);
    }


    @Transactional
    public void reset(Long id) {
        repo.deleteAll();
    }
}
