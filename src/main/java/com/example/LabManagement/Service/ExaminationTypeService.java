package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Exception.ExaminationTypeNotFoundException;
import com.example.LabManagement.Exception.DuplicateExaminationTypeException;
import com.example.LabManagement.Exception.ExaminationCategoryNotFoundException;
import com.example.LabManagement.Repository.ExaminationTypeRepository;
import com.example.LabManagement.Repository.ExaminationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationTypeService {

    private final ExaminationTypeRepository typeRepository;
    private final ExaminationCategoryRepository categoryRepository;

    @Transactional
    public ExaminationType createType(ExaminationType type, Long categoryId) {
        if (typeRepository.existsByName(type.getName())) {
            throw new DuplicateExaminationTypeException("Examination type '" + type.getName() + "' already exists");
        }

        ExaminationCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ExaminationCategoryNotFoundException("Category not found with ID: " + categoryId));

        type.setExaminationCategory(category);
        return typeRepository.save(type);
    }

    public ExaminationType findById(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new ExaminationTypeNotFoundException("Examination type not found: " + id));
    }

    public ExaminationType findByName(String name) {
        return typeRepository.findByName(name)
                .orElseThrow(() -> new ExaminationTypeNotFoundException("Type not found: " + name));
    }

    public List<ExaminationType> getAllTypes() {
        return typeRepository.findAll();
    }

    public List<ExaminationType> getTypesByCategory(Long categoryId) {
        return typeRepository.findByExaminationCategoryId(categoryId);
    }

    @Transactional
    public ExaminationType updateType(Long id, ExaminationType updated) {
        ExaminationType existing = findById(id);

        if (!existing.getName().equals(updated.getName())) {
            if (typeRepository.existsByName(updated.getName())) {
                throw new DuplicateExaminationTypeException("Type name already exists: " + updated.getName());
            }
        }

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setUnit(updated.getUnit());
        existing.setReferenceRange(updated.getReferenceRange());

        return typeRepository.save(existing);
    }

    @Transactional
    public void deleteType(Long id) {
        ExaminationType type = findById(id);
        typeRepository.delete(type);
    }

    public boolean existsByName(String name) {
        return typeRepository.existsByName(name);
    }

    public long getTotalCount() {
        return typeRepository.count();
    }
}