package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Role;
import com.example.LabManagement.Service.DepartmentService;
import com.example.LabManagement.Service.DoctorService;
import com.example.LabManagement.dto.DoctorForm;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDoctorController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listDoctors(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "doctors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("doctorForm", new DoctorForm());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "doctors/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findDoctorById(id);
        DoctorForm form = new DoctorForm();
        form.setId(doctor.getId());
        form.setFirstName(doctor.getFirstName());
        form.setLastName(doctor.getLastName());
        form.setIdNumber(doctor.getIdNumber());
        form.setEmail(doctor.getEmail());
        form.setPhone(doctor.getPhone());
        form.setSpecialization(doctor.getSpecialization());
        form.setLicenseNumber(doctor.getLicenseNumber());
        if (doctor.getDepartment() != null) {
            form.setDepartmentId(doctor.getDepartment().getId());
        }
        model.addAttribute("doctorForm", form);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("doctor", doctor);
        return "doctors/form";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute("doctorForm") @Valid DoctorForm form,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "doctors/form";
        }

        try {
            if (form.getId() != null) {
                // Update existing doctor
                Doctor existing = doctorService.findDoctorById(form.getId());
                existing.setFirstName(form.getFirstName());
                existing.setLastName(form.getLastName());
                existing.setIdNumber(form.getIdNumber());
                existing.setEmail(form.getEmail());
                existing.setPhone(form.getPhone());
                existing.setSpecialization(form.getSpecialization());
                existing.setLicenseNumber(form.getLicenseNumber());
                if (form.getDepartmentId() != null) {
                    existing.setDepartment(departmentService.findDepartmentById(form.getDepartmentId()));
                }
                doctorService.updateDoctor(form.getId(), existing);
            } else {
                // Create new doctor
                Doctor newDoctor = new Doctor();
                newDoctor.setFirstName(form.getFirstName());
                newDoctor.setLastName(form.getLastName());
                newDoctor.setIdNumber(form.getIdNumber());
                newDoctor.setEmail(form.getEmail());
                newDoctor.setPhone(form.getPhone());
                newDoctor.setSpecialization(form.getSpecialization());
                newDoctor.setLicenseNumber(form.getLicenseNumber());
                if (form.getDepartmentId() != null) {
                    newDoctor.setDepartment(departmentService.findDepartmentById(form.getDepartmentId()));
                }
                
                // Create UserAccount if username and password are provided
                if (form.getUsername() != null && !form.getUsername().isBlank() && 
                    form.getPassword() != null && !form.getPassword().isBlank()) {
                    // Check if username already exists
                    if (userAccountRepository.findByUsername(form.getUsername().trim()).isPresent()) {
                        bindingResult.rejectValue("username", "doctorForm.username", "Username already exists");
                        model.addAttribute("departments", departmentService.getAllDepartments());
                        return "doctors/form";
                    }
                    
                    UserAccount userAccount = new UserAccount();
                    userAccount.setUsername(form.getUsername().trim());
                    userAccount.setPassword(passwordEncoder.encode(form.getPassword()));
                    userAccount.setRole(Role.DOCTOR);
                    userAccount = userAccountRepository.save(userAccount);
                    newDoctor.setUserAccount(userAccount);
                }
                
                doctorService.registerDoctor(newDoctor);
            }
            return "redirect:/doctors?success";
        } catch (Exception ex) {
            bindingResult.rejectValue("idNumber", "doctorForm.idNumber", ex.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "doctors/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors?deleted";
    }
}

