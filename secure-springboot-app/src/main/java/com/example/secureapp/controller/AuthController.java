package com.example.secureapp.controller;

import com.example.secureapp.model.AppUser;
import com.example.secureapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        String email = body.getOrDefault("email", null);

        if (username.isEmpty() || password.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid username or password"));
        }
        AppUser created = userService.createUser(username, password, email);
        return ResponseEntity.ok(Map.of("id", created.getId(), "username", created.getUsername()));
    }

    // For session-based login, Spring Security handles POST /api/auth/login automatically (see SecurityConfig).
    // We can also add an endpoint to check login state:
    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }
        return ResponseEntity.ok(Map.of("authenticated", true));
    }
}
