package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.ERole;
import com.example.eventmanagement_backend.entity.Role;
import com.example.eventmanagement_backend.entity.User;
import com.example.eventmanagement_backend.payload.request.LoginRequest;
import com.example.eventmanagement_backend.payload.request.SignupRequest;
import com.example.eventmanagement_backend.payload.response.JwtResponse;
import com.example.eventmanagement_backend.payload.response.MessageResponse;
import com.example.eventmanagement_backend.repository.RoleRepository;
import com.example.eventmanagement_backend.repository.UserRepository;
import com.example.eventmanagement_backend.security.JwtUtils;
import com.example.eventmanagement_backend.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "AuthenticationController",
        description = "Manage the authentication process to log and reg in the user.")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    // Consolidating redundant PasswordEncoder injections (using 'encoder')
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "LogIn REST API",
            description = "User data check with user exist or not in database.")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Build response payload
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", jwt);
        response.put("id", userDetails.getId());
        response.put("email", userDetails.getEmail());
        response.put("username", userDetails.getUsername());
        response.put("role", userDetails.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER"));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registration REST API",
            description = "Collect user info and store in database.")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account and encode the password
        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        // --- Role Assignment based on user selection ---
        ERole requestedRole = ERole.ROLE_USER; // Default to USER

        if (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) {
            try {
                requestedRole = ERole.valueOf(signUpRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Invalid role specified."));
            }
        }

        // Capture the determined role in a final variable to satisfy the lambda expression requirement.

        Role userRole = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));

        user.setRole(userRole);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully as " + requestedRole.name() + "!"));
    }

    @Operation(summary = " Current user data share REST API",
            description = "Get user info from the database and circulate it around the application.")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Unauthorized"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        return ResponseEntity.ok(new JwtResponse(
                null, // no new token here
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role
        ));
    }

}
