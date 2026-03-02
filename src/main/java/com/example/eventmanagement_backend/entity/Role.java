package com.example.eventmanagement_backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// Add @Entity and @Table
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Role ID")
        private Integer id;

        @Enumerated(EnumType.STRING)
        @Column(length = 20)
        @Schema(description = "Role name")
        private ERole name;

        // Constructor (if manually defined or if using lombok's @RequiredArgsConstructor)
        public Role(ERole name) {
                this.name = name;
        }
}
