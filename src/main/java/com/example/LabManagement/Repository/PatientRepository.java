package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByIdNumber(String idNumber);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhone(String phone);

    boolean existsByIdNumber(String idNumber);

    boolean existsByEmail(String email);

    List<Patient> findByGender(String gender);

    Optional<Patient> findByUserAccountId(Long userAccountId);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.fathersName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchByName(@Param("searchTerm") String searchTerm);
}