package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByIdNumber(String idNumber);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    boolean existsByIdNumber(String idNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Doctor> findByDepartmentId(Long departmentId);

    Optional<Doctor> findByUserAccountId(Long userAccountId);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> searchByName(String name);
}
