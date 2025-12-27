package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationTypeRepository extends JpaRepository<ExaminationType, Long> {

    Optional<ExaminationType> findByName(String name);

    boolean existsByName(String name);

    List<ExaminationType> findByExaminationCategoryId(Long categoryId);
}