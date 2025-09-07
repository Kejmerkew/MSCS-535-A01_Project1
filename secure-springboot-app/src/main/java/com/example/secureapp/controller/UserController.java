package com.example.secureapp.controller;

import com.example.secureapp.model.AppUser;
import com.example.secureapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    // GET own profile
    @GetMapping("/me")
    public Object me(Authentication auth) {
        String username = auth.getName();
        return repo.findByUsername(username)
                .map(u -> Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail()))
                .orElse(Map.of("error", "not found"));
    }

    // safe search example using JPA derived query (parameterized)
    @GetMapping("/search")
    public Object search(@RequestParam("q") String q) {
        if (q == null || q.trim().length() < 1) {
            return List.of();
        }
        List<?> users = repo.findTop50ByUsernameContainingIgnoreCase(q.trim())
                .stream()
                .map(u -> Map.of("id", u.getId(), "username", u.getUsername()))
                .collect(Collectors.toList());
        return users;
    }
}
