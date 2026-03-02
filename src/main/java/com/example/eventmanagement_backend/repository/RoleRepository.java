package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.ERole;
import com.example.eventmanagement_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
