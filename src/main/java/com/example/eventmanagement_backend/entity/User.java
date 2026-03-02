package com.example.eventmanagement_backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "User email")
    private String email;

    @Schema(description = "User name")
    private String name;

    @Schema(description = "User password")
    private String password; // Will be stored as a hashed value

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    //  All-Argument Constructor (for Hibernate/JPA retrieval)
    // You MUST define this manually since you have another custom constructor
    public User(Long id, String email, String name, String password, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Custom Registration Constructor (for AuthController)
    public User(String name, String email, String encodedPassword) {
        this.name = name;
        this.email = email;
        this.password = encodedPassword; // Assign the encoded password
        // Role is set via setter in AuthController
    }
}
