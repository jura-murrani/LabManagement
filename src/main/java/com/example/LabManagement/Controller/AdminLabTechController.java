package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.LabTech;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Role;
import com.example.LabManagement.Service.DepartmentService;
import com.example.LabManagement.Service.LabTechService;
import com.example.LabManagement.dto.LabTechForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/labtechs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLabTechController {

    private final LabTechService labTechService;
    private final DepartmentService departmentService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listLabTechs(Model model) {
        List<LabTech> labTechs = labTechService.getAllLabTechs();
        model.addAttribute("labTechs", labTechs);
        return "labtechs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("labTechForm", new LabTechForm());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "labtechs/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        LabTech labTech = labTechService.findLabTechById(id);
        LabTechForm form = new LabTechForm();
        form.setId(labTech.getId());
        form.setFirstName(labTech.getFirstName());
        form.setLastName(labTech.getLastName());
        form.setIdNumber(labTech.getIdNumber());
        form.setEmail(labTech.getEmail());
        form.setPhone(labTech.getPhone());
        form.setQualification(labTech.getQualification());
        if (labTech.getDepartment() != null) {
            form.setDepartmentId(labTech.getDepartment().getId());
        }
        model.addAttribute("labTechForm", form);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("labTech", labTech);
        return "labtechs/form";
    }

    @PostMapping("/save")
    public String saveLabTech(@ModelAttribute("labTechForm") @Valid LabTechForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "labtechs/form";
        }

        try {
            if (form.getId() != null) {
                // Update existing lab tech
                LabTech existing = labTechService.findLabTechById(form.getId());
                existing.setFirstName(form.getFirstName());
                existing.setLastName(form.getLastName());
                existing.setIdNumber(form.getIdNumber());
                existing.setEmail(form.getEmail());
                existing.setPhone(form.getPhone());
                existing.setQualification(form.getQualification());
                if (form.getDepartmentId() != null) {
                    existing.setDepartment(departmentService.findDepartmentById(form.getDepartmentId()));
                } else {
                    existing.setDepartment(null);
                }
                labTechService.updateLabTech(form.getId(), existing);
            } else {
                // Create new lab tech
                LabTech newLabTech = new LabTech();
                newLabTech.setFirstName(form.getFirstName());
                newLabTech.setLastName(form.getLastName());
                newLabTech.setIdNumber(form.getIdNumber());
                newLabTech.setEmail(form.getEmail());
                newLabTech.setPhone(form.getPhone());
                newLabTech.setQualification(form.getQualification());
                if (form.getDepartmentId() != null) {
                    newLabTech.setDepartment(departmentService.findDepartmentById(form.getDepartmentId()));
                }
                
                // Create UserAccount if username and password are provided
                if (form.getUsername() != null && !form.getUsername().isBlank() && 
                    form.getPassword() != null && !form.getPassword().isBlank()) {
                    // Check if username already exists
                    if (userAccountRepository.findByUsername(form.getUsername().trim()).isPresent()) {
                        bindingResult.rejectValue("username", "labTechForm.username", "Username already exists");
                        model.addAttribute("departments", departmentService.getAllDepartments());
                        return "labtechs/form";
                    }
                    
                    UserAccount userAccount = new UserAccount();
                    userAccount.setUsername(form.getUsername().trim());
                    userAccount.setPassword(passwordEncoder.encode(form.getPassword()));
                    userAccount.setRole(Role.LAB_TECHNICIAN);
                    userAccount = userAccountRepository.save(userAccount);
                    newLabTech.setUserAccount(userAccount);
                }
                
                labTechService.registerLabTech(newLabTech);
            }
            return "redirect:/labtechs?success";
        } catch (Exception ex) {
            bindingResult.rejectValue("email", "labTechForm.email", ex.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "labtechs/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteLabTech(@PathVariable Long id) {
        labTechService.deleteLabTech(id);
        return "redirect:/labtechs?deleted";
    }
}

