package com.example.LabManagement.Repository;

import com.example.LabManagement.AnalysisStatus;
import com.example.LabManagement.Entity.AnalysisOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisOrderRepository extends JpaRepository<AnalysisOrder, Long> {

    List<AnalysisOrder> findByVisitId(Long visitId);
    
    List<AnalysisOrder> findByStatus(AnalysisStatus status);
    
    List<AnalysisOrder> findByPatientId(Long patientId);
    
    List<AnalysisOrder> findByStatusAndDoctorIsNull(AnalysisStatus status);
    
    List<AnalysisOrder> findByDoctorIdAndStatus(Long doctorId, AnalysisStatus status);

}
