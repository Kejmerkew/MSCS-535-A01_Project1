package com.example.secureapp.repository;

import com.example.secureapp.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    // safe parameter binding in derived query
    List<AppUser> findTop50ByUsernameContainingIgnoreCase(String partial);
}
