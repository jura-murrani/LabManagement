package com.example.LabManagement.Repository;

import com.example.LabManagement.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByName(String name);

    Optional<Department> findByName(String name);
}
