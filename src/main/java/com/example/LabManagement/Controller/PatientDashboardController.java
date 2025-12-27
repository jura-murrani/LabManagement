package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.AnalysisResult;
import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.LabTech;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Repository.PatientRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisResultService;
import com.example.LabManagement.Service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientDashboardController {

    private final AnalysisResultService analysisResultService;
    private final PatientRepository patientRepository;
    private final UserAccountRepository userAccountRepository;
    private final VisitService visitService;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        // Get patient's results
        List<AnalysisResult> results = analysisResultService.getResultsReadyForPatient(patient.getId());
        
        // Get all visits for this patient to find doctors
        List<Visit> visits = visitService.getVisitsByPatientId(patient.getId());
        Set<Doctor> doctors = visits.stream()
                .map(Visit::getDoctor)
                .filter(doctor -> doctor != null)
                .collect(Collectors.toSet());
        
        // Get lab technicians from results
        Set<LabTech> labTechs = results.stream()
                .map(AnalysisResult::getLabTech)
                .filter(labTech -> labTech != null)
                .collect(Collectors.toSet());
        
        model.addAttribute("patient", patient);
        model.addAttribute("results", results);
        model.addAttribute("doctors", doctors);
        model.addAttribute("labTechs", labTechs);
        return "patient/dashboard";
    }
}

