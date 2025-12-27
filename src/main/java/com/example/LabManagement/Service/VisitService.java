package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Exception.DoctorNotFoundException;
import com.example.LabManagement.Exception.PatientNotFoundException;
import com.example.LabManagement.Exception.VisitNotFoundException;
import com.example.LabManagement.Repository.AnalysisOrderRepository;
import com.example.LabManagement.Repository.VisitRepository;
import com.example.LabManagement.Repository.PatientRepository;
import com.example.LabManagement.Repository.DoctorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AnalysisOrderRepository analysisOrderRepository;

    /**
     * Create a new visit
     */
    @Transactional
    public Visit createVisit(Long patientId, Long doctorId, LocalDateTime visitDate, String diagnosis, String notes) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID " + patientId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID " + doctorId));

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setVisitDate(visitDate);
        visit.setDiagnosis(diagnosis);
        visit.setNotes(notes);

        return visitRepository.save(visit);
    }
    /**
     * Add an AnalysisOrder to a specific visit
     */
    @Transactional
    public AnalysisOrder addAnalysisOrderToVisit(Long visitId, AnalysisOrder order) {
        Visit visit = findVisitById(visitId);

        order.setVisit(visit); // link order to visit
        visit.getAnalysisOrders().add(order); // add to visit's list

        return analysisOrderRepository.save(order);
    }

    /**
     * Find visit by ID
     */
    public Visit findVisitById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visit with ID " + id + " not found"));
    }

    /**
     * Get all visits
     */
    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    /**
     * Get all visits of a patient
     */
    public List<Visit> getVisitsByPatientId(Long patientId) {
        return visitRepository.findByPatientId(patientId);
    }

    /**
     * Get all visits of a doctor
     */
    public List<Visit> getVisitsByDoctorId(Long doctorId) {
        return visitRepository.findByDoctorId(doctorId);
    }
    /**
     * Get all analysis orders of a specific visit
     */
    public List<AnalysisOrder> getOrdersForVisit(Long visitId) {
        Visit visit = findVisitById(visitId);
        return visit.getAnalysisOrders();
    }

    /**
     * Update visit
     */
    @Transactional
    public Visit updateVisit(Long id, LocalDateTime visitDate, String diagnosis, String notes) {
        Visit visit = findVisitById(id);

        if (visitDate != null) {
            visit.setVisitDate(visitDate);
        }
        if (diagnosis != null) {
            visit.setDiagnosis(diagnosis);
        }
        if (notes != null) {
            visit.setNotes(notes);
        }

        return visitRepository.save(visit);
    }

    /**
     * Delete visit
     */
    @Transactional
    public void deleteVisit(Long id) {
        Visit visit = findVisitById(id);
        visitRepository.delete(visit);
    }

    /**
     * Total visit count
     */
    public long getTotalVisitCount() {
        return visitRepository.count();
    }
}
