package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.LabTech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTechRepository extends JpaRepository<LabTech, Long> {

    Optional<LabTech> findByEmail(String email);

    boolean existsByEmail(String email);
    
    Optional<LabTech> findByUserAccountId(Long userAccountId);
}