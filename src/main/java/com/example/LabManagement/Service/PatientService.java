package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Exception.DuplicatePatientException;
import com.example.LabManagement.Exception.PatientNotFoundException;
import com.example.LabManagement.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {
    private final PatientRepository patientRepository;

    /**
     * Register a new patient in the system
     */
    @Transactional
    public Patient registerPatient(Patient patient) {
        // Validate unique ID number
        if (patientRepository.existsByIdNumber(patient.getIdNumber())) {
            throw new DuplicatePatientException("Patient with ID number " + patient.getIdNumber() + " already exists");
        }
        return patientRepository.save(patient);
    }

    /**
     * Find patient by ID
     */
    public Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found"));
    }

    /**
     * Find patient by ID number (national ID)
     */
    public Patient findPatientByIdNumber(String idNumber) {
        return patientRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID number " + idNumber + " not found"));
    }

    /**
     * Search patients by name (first, father's, or last name)
     */
    public List<Patient> searchPatientsByName(String searchTerm) {
        return patientRepository.searchByName(searchTerm);
    }

    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Update patient information
     */
    @Transactional
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient existingPatient = findPatientById(id);

        // Check if ID number is being changed and if it's unique
        if (!existingPatient.getIdNumber().equals(updatedPatient.getIdNumber())) {
            if (patientRepository.existsByIdNumber(updatedPatient.getIdNumber())) {
                throw new DuplicatePatientException("Patient with ID number " + updatedPatient.getIdNumber() + " already exists");
            }
        }

        // Update fields
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setFathersName(updatedPatient.getFathersName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setIdNumber(updatedPatient.getIdNumber());
        existingPatient.setBirthDate(updatedPatient.getBirthDate());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setPhone(updatedPatient.getPhone());

        return patientRepository.save(existingPatient);
    }

    /**
     * Update patient contact information only
     */
    @Transactional
    public Patient updatePatientContact(Long id, String email, String phone) {
        Patient patient = findPatientById(id);

        if (email != null) {
            patient.setEmail(email);
        }
        if (phone != null) {
            patient.setPhone(phone);
        }

        return patientRepository.save(patient);
    }

    /**
     * Soft delete or deactivate patient
     */
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = findPatientById(id);
        patientRepository.delete(patient);
    }

    /**
     * Check if patient exists by ID number
     */
    public boolean patientExistsByIdNumber(String idNumber) {
        return patientRepository.existsByIdNumber(idNumber);
    }

    /**
     * Get patient's age
     */
    public int getPatientAge(Long id) {
        Patient patient = findPatientById(id);
        return patient.getYearsOld();
    }

    /**
     * Get total count of patients
     */
    public long getTotalPatientCount() {
        return patientRepository.count();
    }

    /**
     * Find patient by associated user account
     */
    public Optional<Patient> findPatientByUserAccountId(Long userAccountId) {
        return patientRepository.findByUserAccountId(userAccountId);
    }
}