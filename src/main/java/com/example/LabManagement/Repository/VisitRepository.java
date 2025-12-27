package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByPatientId(Long patientId);

    List<Visit> findByDoctorId(Long doctorId);
}
