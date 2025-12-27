package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Service.ExaminationCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/examination-categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminExaminationCategoryController {

    private final ExaminationCategoryService examinationCategoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<ExaminationCategory> categories = examinationCategoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/examination-categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Categories are predefined and cannot be created manually
        return "redirect:/admin/examination-categories?predefined";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ExaminationCategory category = examinationCategoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/examination-categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") ExaminationCategory category,
                              BindingResult bindingResult,
                              Model model) {
        // Manual validation
        if (category.getName() == null || category.getName().isBlank()) {
            model.addAttribute("error", "Category name is required");
            return "admin/examination-categories/form";
        }

        try {
            if (category.getId() != null) {
                // Only allow updating description for existing categories
                examinationCategoryService.updateCategory(category.getId(), category);
                return "redirect:/admin/examination-categories?success";
            } else {
                // Prevent creation of new categories - they are predefined
                return "redirect:/admin/examination-categories?predefined";
            }
        } catch (IllegalArgumentException ex) {
            // Handle validation errors for non-allowed categories
            return "redirect:/admin/examination-categories?error=" + 
                   java.net.URLEncoder.encode(ex.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "admin/examination-categories/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        try {
            examinationCategoryService.deleteCategory(id);
            return "redirect:/admin/examination-categories?deleted";
        } catch (IllegalArgumentException ex) {
            return "redirect:/admin/examination-categories?error=" + 
                   java.net.URLEncoder.encode(ex.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}

