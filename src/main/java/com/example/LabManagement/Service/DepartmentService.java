package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.Department;
import com.example.LabManagement.Exception.DepartmentNotFoundException;
import com.example.LabManagement.Exception.DuplicateDepartmentException;
import com.example.LabManagement.Repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * Register a new department
     */
    @Transactional
    public Department registerDepartment(Department department) {

        // Check uniqueness by department name
        if (departmentRepository.existsByName(department.getName())) {
            throw new DuplicateDepartmentException(
                    "Department with name '" + department.getName() + "' already exists");
        }

        return departmentRepository.save(department);
    }

    /**
     * Get department by ID
     */
    public Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new DepartmentNotFoundException("Department with ID " + id + " not found"));
    }

    /**
     * Get department by name
     */
    public Department findDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() ->
                        new DepartmentNotFoundException("Department with name '" + name + "' not found"));
    }

    /**
     * Get all departments
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Update department info
     */
    @Transactional
    public Department updateDepartment(Long id, Department updatedDepartment) {

        Department existingDepartment = findDepartmentById(id);

        // Check name uniqueness if changed
        if (!existingDepartment.getName().equals(updatedDepartment.getName())) {
            if (departmentRepository.existsByName(updatedDepartment.getName())) {
                throw new DuplicateDepartmentException(
                        "Department with name '" + updatedDepartment.getName() + "' already exists");
            }
        }

        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setDescription(updatedDepartment.getDescription());

        return departmentRepository.save(existingDepartment);
    }

    /**
     * Delete a department
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = findDepartmentById(id);
        departmentRepository.delete(department);
    }

    /**
     * Check if department name exists
     */
    public boolean departmentExistsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    /**
     * Total department count
     */
    public long getTotalDepartmentCount() {
        return departmentRepository.count();
    }
}
