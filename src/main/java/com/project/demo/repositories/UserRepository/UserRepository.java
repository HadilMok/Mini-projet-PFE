package com.project.demo.repositories.UserRepository;


import com.project.demo.models.Enumerations.Role;
import com.project.demo.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findAllByAvailable(boolean available);
    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmail(String email);
    List<UserAccount> findAllByRole(Role role);
}
