package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Exception.DoctorNotFoundException;
import com.example.LabManagement.Exception.DuplicateDoctorException;
import com.example.LabManagement.Repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;

    /**
     * Register a new doctor in the system
     */
    @Transactional
    public Doctor registerDoctor(Doctor doctor) {

        // Check ID number uniqueness
        if (doctorRepository.existsByIdNumber(doctor.getIdNumber())) {
            throw new DuplicateDoctorException(
                    "Doctor with ID number " + doctor.getIdNumber() + " already exists"
            );
        }

        // Check license number uniqueness
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new DuplicateDoctorException(
                    "Doctor with license number " + doctor.getLicenseNumber() + " already exists"
            );
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Find doctor by ID
     */
    public Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor with ID " + id + " not found"));
    }

    /**
     * Find doctor by national ID number
     */
    public Doctor findDoctorByIdNumber(String idNumber) {
        return doctorRepository.findByIdNumber(idNumber)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor with ID number " + idNumber + " not found"));
    }

    /**
     * Find doctor by license number
     */
    public Doctor findDoctorByLicenseNumber(String licenseNumber) {
        return doctorRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() ->
                        new DoctorNotFoundException("Doctor with license number " + licenseNumber + " not found"));
    }

    /**
     * Search by doctor's first or last name
     */
    public List<Doctor> searchDoctorsByName(String searchTerm) {
        return doctorRepository.searchByName(searchTerm);
    }

    /**
     * Find all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Update doctor information
     */
    @Transactional
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {

        Doctor existingDoctor = findDoctorById(id);

        // Check ID number uniqueness
        if (!existingDoctor.getIdNumber().equals(updatedDoctor.getIdNumber())) {
            if (doctorRepository.existsByIdNumber(updatedDoctor.getIdNumber())) {
                throw new DuplicateDoctorException(
                        "Doctor with ID number " + updatedDoctor.getIdNumber() + " already exists"
                );
            }
        }

        // Check license number uniqueness
        if (!existingDoctor.getLicenseNumber().equals(updatedDoctor.getLicenseNumber())) {
            if (doctorRepository.existsByLicenseNumber(updatedDoctor.getLicenseNumber())) {
                throw new DuplicateDoctorException(
                        "Doctor with license number " + updatedDoctor.getLicenseNumber() + " already exists"
                );
            }
        }

        // Update fields
        existingDoctor.setFirstName(updatedDoctor.getFirstName());
        existingDoctor.setLastName(updatedDoctor.getLastName());
        existingDoctor.setIdNumber(updatedDoctor.getIdNumber());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setPhone(updatedDoctor.getPhone());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setLicenseNumber(updatedDoctor.getLicenseNumber());
        existingDoctor.setDepartment(updatedDoctor.getDepartment());
        existingDoctor.setUserAccount(updatedDoctor.getUserAccount());

        return doctorRepository.save(existingDoctor);
    }

    /**
     * Delete doctor
     */
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorById(id);
        doctorRepository.delete(doctor);
    }

    /**
     * Check if doctor exists by ID number
     */
    public boolean doctorExistsByIdNumber(String idNumber) {
        return doctorRepository.existsByIdNumber(idNumber);
    }

    /**
     * Check if doctor exists by license number
     */
    public boolean doctorExistsByLicenseNumber(String licenseNumber) {
        return doctorRepository.existsByLicenseNumber(licenseNumber);
    }

    /**
     * Find doctors in a department
     */
    public List<Doctor> findDoctorsByDepartmentId(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }

    /**
     * Find doctor by associated user account
     */
    public Optional<Doctor> findDoctorByUserAccountId(Long userAccountId) {
        return doctorRepository.findByUserAccountId(userAccountId);
    }

    /**
     * Get total number of doctors
     */
    public long getTotalDoctorCount() {
        return doctorRepository.count();
    }
}
