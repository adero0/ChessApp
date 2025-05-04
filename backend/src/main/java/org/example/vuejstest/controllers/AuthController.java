package org.example.vuejstest.controllers;

import org.example.vuejstest.models.AuthResponse;
import org.example.vuejstest.models.User;
import org.example.vuejstest.models.enums.Role;
import org.example.vuejstest.repository.UserRepository;
import org.example.vuejstest.jwt.JwtUtil;
import org.example.vuejstest.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            // Authenticate the user
            log.info("Login attempt for username: {}", user.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            // Load UserDetails after successful authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            log.info("User authenticated, generating token for: {}", userDetails.getUsername());
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
        } catch (AuthenticationException e) {
            log.error("Login failed for {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try{
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    } catch (DataIntegrityViolationException e) {
        log.warn("Username {} already exists", user.getUsername());
        return ResponseEntity.status(400).body("Username already exists");
    } catch (Exception e) {
        log.error("Registration failed", e);
        return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
    }
    }
}