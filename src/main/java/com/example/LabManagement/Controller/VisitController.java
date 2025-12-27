package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Repository.DoctorRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisOrderService;
import com.example.LabManagement.Service.ExaminationTypeService;
import com.example.LabManagement.Service.PatientService;
import com.example.LabManagement.Service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/visits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class VisitController {

    private final VisitService visitService;
    private final PatientService patientService;
    private final DoctorRepository doctorRepository;
    private final UserAccountRepository userAccountRepository;
    private final ExaminationTypeService examinationTypeService;
    private final AnalysisOrderService analysisOrderService;

    @GetMapping
    public String listVisits(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Doctor doctor = doctorRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for user"));
        
        List<Visit> visits = visitService.getVisitsByDoctorId(doctor.getId());
        model.addAttribute("visits", visits);
        return "visits/list";
    }

    @GetMapping("/new")
    public String showSelectPatientForm(Model model, @RequestParam(required = false) String search) {
        List<com.example.LabManagement.Entity.Patient> patients;
        if (search != null && !search.isBlank()) {
            patients = patientService.searchPatientsByName(search);
        } else {
            patients = patientService.getAllPatients();
        }
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "visits/select-patient";
    }

    @GetMapping("/new/{patientId}")
    public String showCreateVisitForm(@PathVariable Long patientId, Model model) {
        model.addAttribute("patient", patientService.findPatientById(patientId));
        model.addAttribute("examinationTypes", examinationTypeService.getAllTypes());
        return "visits/form";
    }

    @PostMapping("/new/{patientId}")
    public String createVisit(@PathVariable Long patientId,
                             @RequestParam String diagnosis,
                             @RequestParam(required = false) String notes,
                             @RequestParam(required = false) List<Long> examinationTypeIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Doctor doctor = doctorRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for user"));
        
        Visit visit = visitService.createVisit(patientId, doctor.getId(), LocalDateTime.now(), diagnosis, notes);
        
        // Add all selected examination types as analysis orders
        if (examinationTypeIds != null && !examinationTypeIds.isEmpty()) {
            for (Long examTypeId : examinationTypeIds) {
                ExaminationType examType = examinationTypeService.findById(examTypeId);
                analysisOrderService.createAnalysisOrder(visit.getId(), examType);
            }
        }
        
        return "redirect:/visits/" + visit.getId();
    }

    @GetMapping("/{id}")
    public String viewVisit(@PathVariable Long id, Model model) {
        Visit visit = visitService.findVisitById(id);
        List<ExaminationType> examinationTypes = examinationTypeService.getAllTypes();
        
        model.addAttribute("visit", visit);
        model.addAttribute("examinationTypes", examinationTypes);
        return "visits/view";
    }

    @PostMapping("/{id}/orders")
    public String addAnalysisOrder(@PathVariable Long id, @RequestParam Long examinationTypeId) {
        ExaminationType examType = examinationTypeService.findById(examinationTypeId);
        analysisOrderService.createAnalysisOrder(id, examType);
        return "redirect:/visits/" + id;
    }

    @PostMapping("/{id}/update")
    public String updateVisit(@PathVariable Long id,
                             @RequestParam(required = false) String diagnosis,
                             @RequestParam(required = false) String notes) {
        visitService.updateVisit(id, null, diagnosis, notes);
        return "redirect:/visits/" + id;
    }
}

