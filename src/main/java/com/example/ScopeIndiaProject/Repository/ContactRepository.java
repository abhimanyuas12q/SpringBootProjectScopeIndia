package com.example.ScopeIndiaProject.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ScopeIndiaProject.Model.ContactMail;

@Repository
public interface ContactRepository extends JpaRepository<ContactMail, Long>{
    
}

