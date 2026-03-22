package com.project.demo.repositories.ProjectsRepository;

import com.project.demo.models.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectsRepository extends JpaRepository<Projects, Long> {
    Boolean existsByCode(String code);
}