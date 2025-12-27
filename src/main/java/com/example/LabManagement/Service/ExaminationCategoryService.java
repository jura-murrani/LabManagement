package com.example.LabManagement.Service;

import com.example.LabManagement.Constants.ExaminationCategories;
import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Exception.ExaminationCategoryNotFoundException;
import com.example.LabManagement.Exception.DuplicateExaminationCategoryException;
import com.example.LabManagement.Repository.ExaminationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationCategoryService {

    private final ExaminationCategoryRepository categoryRepository;

    @Transactional
    public ExaminationCategory createCategory(ExaminationCategory category) {
        // Validate that only predefined categories can be created
        if (!ExaminationCategories.isAllowed(category.getName())) {
            throw new IllegalArgumentException(
                    "Category creation is restricted. Only predefined categories are allowed: " + 
                    String.join(", ", ExaminationCategories.ALLOWED_CATEGORIES));
        }
        
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateExaminationCategoryException(
                    "Category '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    public ExaminationCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ExaminationCategoryNotFoundException("Category not found with ID: " + id));
    }

    public ExaminationCategory findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ExaminationCategoryNotFoundException("Category not found: " + name));
    }

    public List<ExaminationCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public ExaminationCategory updateCategory(Long id, ExaminationCategory updated) {
        ExaminationCategory existing = findById(id);

        // Prevent changing category name to a non-allowed category
        if (!existing.getName().equals(updated.getName())) {
            if (!ExaminationCategories.isAllowed(updated.getName())) {
                throw new IllegalArgumentException(
                        "Category name cannot be changed to a non-allowed category. " +
                        "Only predefined categories are allowed: " + 
                        String.join(", ", ExaminationCategories.ALLOWED_CATEGORIES));
            }
            if (categoryRepository.existsByName(updated.getName())) {
                throw new DuplicateExaminationCategoryException(
                        "Category name '" + updated.getName() + "' is already taken");
            }
        }

        // Only allow updating description, not the name (unless it's still an allowed category)
        if (!existing.getName().equals(updated.getName()) && ExaminationCategories.isAllowed(updated.getName())) {
            existing.setName(updated.getName());
        }
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(Long id) {
        ExaminationCategory category = findById(id);
        
        // Prevent deletion of predefined categories
        if (ExaminationCategories.isAllowed(category.getName())) {
            throw new IllegalArgumentException(
                    "Cannot delete predefined category '" + category.getName() + "'. " +
                    "Only custom categories can be deleted, but all categories in this system are predefined.");
        }
        
        // TODO: check if category has examination types before deleting
        categoryRepository.delete(category);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public long getTotalCount() {
        return categoryRepository.count();
    }
}