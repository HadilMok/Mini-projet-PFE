package com.project.demo.repositories.ConfigurationRepository;

import com.project.demo.models.ConfigurationModel;
import com.project.demo.models.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationModel, Long> {

}
