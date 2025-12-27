package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.ExaminationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExaminationCategoryRepository extends JpaRepository<ExaminationCategory, Long> {

    Optional<ExaminationCategory> findByName(String name);

    boolean existsByName(String name);
}