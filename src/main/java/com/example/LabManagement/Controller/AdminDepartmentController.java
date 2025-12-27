package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.Department;
import com.example.LabManagement.Service.DepartmentService;
import com.example.LabManagement.dto.DepartmentForm;
import jakarta.validation.Valid;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "departments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        return "departments/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.findDepartmentById(id);
        DepartmentForm form = new DepartmentForm();
        form.setId(department.getId());
        form.setName(department.getName());
        form.setDescription(department.getDescription());
        model.addAttribute("departmentForm", form);
        model.addAttribute("department", department);
        return "departments/form";
    }

    @PostMapping("/save")
    public String saveDepartment(@ModelAttribute("departmentForm") @Valid DepartmentForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "departments/form";
        }

        try {
            if (form.getId() != null) {
                // Update existing department
                Department existing = departmentService.findDepartmentById(form.getId());
                existing.setName(form.getName());
                existing.setDescription(form.getDescription());
                departmentService.updateDepartment(form.getId(), existing);
            } else {
                // Create new department
                Department newDepartment = new Department();
                newDepartment.setName(form.getName());
                newDepartment.setDescription(form.getDescription());
                departmentService.registerDepartment(newDepartment);
            }
            return "redirect:/departments?success";
        } catch (Exception ex) {
            bindingResult.rejectValue("name", "departmentForm.name", ex.getMessage());
            return "departments/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/departments?deleted";
    }
}

