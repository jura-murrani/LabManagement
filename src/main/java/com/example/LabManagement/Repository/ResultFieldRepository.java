package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.ResultField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultFieldRepository extends JpaRepository<ResultField, Long> {
    List<ResultField> findByAnalysisResultId(Long analysisResultId);
}

