package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.LabTech;
import com.example.LabManagement.Exception.LabTechNotFoundException;
import com.example.LabManagement.Exception.DuplicateLabTechException;
import com.example.LabManagement.Repository.LabTechRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTechService {

    private final LabTechRepository labTechRepository;

    @Transactional
    public LabTech registerLabTech(LabTech labTech) {
        if (labTechRepository.existsByEmail(labTech.getEmail())) {
            throw new DuplicateLabTechException("LabTech with email " + labTech.getEmail() + " already exists");
        }
        return labTechRepository.save(labTech);
    }

    public LabTech findLabTechById(Long id) {
        return labTechRepository.findById(id)
                .orElseThrow(() -> new LabTechNotFoundException("LabTech with ID " + id + " not found"));
    }

    public List<LabTech> getAllLabTechs() {
        return labTechRepository.findAll();
    }

    @Transactional
    public LabTech updateLabTech(Long id, LabTech updatedLabTech) {
        LabTech existing = findLabTechById(id);

        if (!existing.getEmail().equals(updatedLabTech.getEmail())) {
            if (labTechRepository.existsByEmail(updatedLabTech.getEmail())) {
                throw new DuplicateLabTechException("Email " + updatedLabTech.getEmail() + " is already in use");
            }
        }

        existing.setFirstName(updatedLabTech.getFirstName());
        existing.setLastName(updatedLabTech.getLastName());
        existing.setIdNumber(updatedLabTech.getIdNumber());
        existing.setEmail(updatedLabTech.getEmail());
        existing.setPhone(updatedLabTech.getPhone());
        existing.setQualification(updatedLabTech.getQualification());
        existing.setDepartment(updatedLabTech.getDepartment());

        return labTechRepository.save(existing);
    }

    @Transactional
    public void deleteLabTech(Long id) {
        LabTech labTech = findLabTechById(id);
        labTechRepository.delete(labTech);
    }

    public boolean existsByEmail(String email) {
        return labTechRepository.existsByEmail(email);
    }
}