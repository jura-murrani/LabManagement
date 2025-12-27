package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Role;
import com.example.LabManagement.Service.PatientService;
import com.example.LabManagement.dto.PatientForm;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPatientController {

    private final PatientService patientService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listPatients(Model model, 
                               @RequestParam(required = false) LocalDate dateFrom,
                               @RequestParam(required = false) LocalDate dateTo) {
        List<Patient> patients = patientService.getAllPatients();
        
        // Filter by birth date range if provided
        if (dateFrom != null || dateTo != null) {
            patients = patients.stream()
                .filter(p -> {
                    if (p.getBirthDate() == null) return false;
                    if (dateFrom != null && p.getBirthDate().isBefore(dateFrom)) return false;
                    if (dateTo != null && p.getBirthDate().isAfter(dateTo)) return false;
                    return true;
                })
                .toList();
        }
        
        model.addAttribute("patients", patients);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        return "patients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        return "patients/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.findPatientById(id);
        PatientForm form = new PatientForm();
        form.setId(patient.getId());
        form.setFirstName(patient.getFirstName());
        form.setFathersName(patient.getFathersName());
        form.setLastName(patient.getLastName());
        form.setIdNumber(patient.getIdNumber());
        form.setBirthDate(patient.getBirthDate());
        form.setGender(patient.getGender());
        form.setEmail(patient.getEmail());
        form.setPhone(patient.getPhone());
        model.addAttribute("patientForm", form);
        model.addAttribute("patient", patient);
        return "patients/form";
    }

    @PostMapping("/save")
    public String savePatient(@ModelAttribute("patientForm") @Valid PatientForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patientForm", form);
            return "patients/form";
        }

        try {
            Patient patient;
            if (form.getId() != null) {
                // Update existing patient
                Patient existing = patientService.findPatientById(form.getId());
                existing.setFirstName(form.getFirstName());
                existing.setFathersName(form.getFathersName());
                existing.setLastName(form.getLastName());
                existing.setIdNumber(form.getIdNumber());
                existing.setBirthDate(form.getBirthDate());
                existing.setGender(form.getGender());
                existing.setEmail(form.getEmail());
                existing.setPhone(form.getPhone());
                patientService.updatePatient(form.getId(), existing);
            } else {
                // Create new patient
                Patient newPatient = new Patient();
                newPatient.setFirstName(form.getFirstName());
                newPatient.setFathersName(form.getFathersName());
                newPatient.setLastName(form.getLastName());
                newPatient.setIdNumber(form.getIdNumber());
                newPatient.setBirthDate(form.getBirthDate());
                newPatient.setGender(form.getGender());
                newPatient.setEmail(form.getEmail());
                newPatient.setPhone(form.getPhone());
                
                // Create UserAccount if username and password are provided
                if (form.getUsername() != null && !form.getUsername().isBlank() && 
                    form.getPassword() != null && !form.getPassword().isBlank()) {
                    // Check if username already exists
                    if (userAccountRepository.findByUsername(form.getUsername().trim()).isPresent()) {
                        bindingResult.rejectValue("username", "patientForm.username", "Username already exists");
                        model.addAttribute("patientForm", form);
                        return "patients/form";
                    }
                    
                    UserAccount userAccount = new UserAccount();
                    userAccount.setUsername(form.getUsername().trim());
                    userAccount.setPassword(passwordEncoder.encode(form.getPassword()));
                    userAccount.setRole(Role.PATIENT);
                    userAccount = userAccountRepository.save(userAccount);
                    newPatient.setUserAccount(userAccount);
                }
                
                patientService.registerPatient(newPatient);
            }
            return "redirect:/patients?success";
        } catch (Exception ex) {
            bindingResult.rejectValue("idNumber", "patientForm.idNumber", ex.getMessage());
            model.addAttribute("patientForm", form);
            return "patients/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients?deleted";
    }
}

