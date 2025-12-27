package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByAnalysisOrderId(Long analysisOrderId);
    
    List<AnalysisResult> findByAnalysisOrder_PatientId(Long patientId);
    
    List<AnalysisResult> findByIsReadyForPatientTrue();
    
    List<AnalysisResult> findByAnalysisOrder_PatientIdAndIsReadyForPatientTrue(Long patientId);
}
