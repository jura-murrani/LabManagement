package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.ExaminationTemplate;
import com.example.LabManagement.Entity.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExaminationTemplateRepository extends JpaRepository<ExaminationTemplate, Long> {
    Optional<ExaminationTemplate> findByExaminationTypeId(Long examinationTypeId);
    Optional<ExaminationTemplate> findByExaminationType(ExaminationType examinationType);
}

