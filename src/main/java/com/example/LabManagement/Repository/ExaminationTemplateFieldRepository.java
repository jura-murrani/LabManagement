package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.ExaminationTemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationTemplateFieldRepository extends JpaRepository<ExaminationTemplateField, Long> {
    List<ExaminationTemplateField> findByTemplateIdOrderByDisplayOrderAsc(Long templateId);
}

