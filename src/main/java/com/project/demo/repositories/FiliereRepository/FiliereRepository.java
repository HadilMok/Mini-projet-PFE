package com.project.demo.repositories.FiliereRepository;

import com.project.demo.models.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    boolean existsByCode(String code);
}
