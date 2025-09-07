package com.example.secureapp.service;

import com.example.secureapp.model.AppUser;
import com.example.secureapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<AppUser> findByUsername(String u) {
        return repo.findByUsername(u);
    }

    @Transactional
    public AppUser createUser(String username, String rawPassword, String email) {
        String hashed = passwordEncoder.encode(rawPassword);
        AppUser u = new AppUser(username, hashed, email);
        return repo.save(u);
    }
}
