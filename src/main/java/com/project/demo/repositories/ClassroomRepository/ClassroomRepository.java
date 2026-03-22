package com.project.demo.repositories.ClassroomRepository;

import com.project.demo.models.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    boolean existsByNumberAndBlocName(Integer number, String blocName);
}
