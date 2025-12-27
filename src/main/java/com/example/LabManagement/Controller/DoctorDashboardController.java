package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Repository.DoctorRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisOrderService;
import com.example.LabManagement.Service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.LabManagement.Entity.UserAccount;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorDashboardController {

    private final VisitService visitService;
    private final DoctorRepository doctorRepository;
    private final UserAccountRepository userAccountRepository;
    private final AnalysisOrderService analysisOrderService;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Doctor doctor = doctorRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for user"));
        
        // Get all visits for this doctor
        List<Visit> visits = visitService.getVisitsByDoctorId(doctor.getId());
        
        // Get unique patients from visits
        Set<Patient> patients = visits.stream()
                .map(Visit::getPatient)
                .collect(Collectors.toSet());
        
        // Create a map of patient to visit count for the template
        Map<Long, Long> visitCounts = visits.stream()
                .collect(Collectors.groupingBy(
                    v -> v.getPatient().getId(),
                    Collectors.counting()
                ));
        
        // Get pending patient requests count
        int pendingRequestCount = analysisOrderService.getPendingPatientRequests().size();
        
        model.addAttribute("patients", patients);
        model.addAttribute("visits", visits);
        model.addAttribute("visitCounts", visitCounts);
        model.addAttribute("doctor", doctor);
        model.addAttribute("pendingRequestCount", pendingRequestCount);
        return "doctor/dashboard";
    }
}

