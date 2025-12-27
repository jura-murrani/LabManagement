package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.AnalysisResult;
import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.PatientRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final AnalysisResultService analysisResultService;
    private final PatientRepository patientRepository;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/results")
    public String listMyResults(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        // Only show results that are ready for patient viewing
        List<AnalysisResult> results = analysisResultService.getResultsReadyForPatient(patient.getId());
        
        // Group results by examination category
        Map<ExaminationCategory, List<AnalysisResult>> resultsByCategory = results.stream()
                .filter(result -> result.getAnalysisOrder() != null 
                        && result.getAnalysisOrder().getExaminationType() != null
                        && result.getAnalysisOrder().getExaminationType().getExaminationCategory() != null)
                .collect(Collectors.groupingBy(
                    result -> result.getAnalysisOrder().getExaminationType().getExaminationCategory()
                ));
        
        // Also handle results without category
        List<AnalysisResult> resultsWithoutCategory = results.stream()
                .filter(result -> result.getAnalysisOrder() == null 
                        || result.getAnalysisOrder().getExaminationType() == null
                        || result.getAnalysisOrder().getExaminationType().getExaminationCategory() == null)
                .collect(Collectors.toList());
        
        model.addAttribute("results", results);
        model.addAttribute("resultsByCategory", resultsByCategory);
        model.addAttribute("resultsWithoutCategory", resultsWithoutCategory);
        model.addAttribute("patient", patient);
        return "patient/results";
    }

    @GetMapping("/results/{id}")
    public String viewResult(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        AnalysisResult result = analysisResultService.getResultById(id);
        
        // Security check: ensure the result belongs to this patient and is ready
        if (!result.getAnalysisOrder().getPatient().getId().equals(patient.getId()) 
            || !Boolean.TRUE.equals(result.getIsReadyForPatient())) {
            return "redirect:/patient/results?accessDenied";
        }
        
        model.addAttribute("result", result);
        return "patient/result-view";
    }
}

