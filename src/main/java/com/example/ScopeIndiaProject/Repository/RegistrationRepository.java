package com.example.ScopeIndiaProject.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ScopeIndiaProject.Model.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    public Registration findByEmail(String email);
}