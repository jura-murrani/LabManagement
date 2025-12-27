package com.example.LabManagement.Controller;

import com.example.LabManagement.AnalysisStatus;
import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.PatientRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisOrderService;
import com.example.LabManagement.Service.ExaminationCategoryService;
import com.example.LabManagement.Service.ExaminationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/patient/analysis")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientAnalysisRequestController {

    private final ExaminationTypeService examinationTypeService;
    private final ExaminationCategoryService examinationCategoryService;
    private final AnalysisOrderService analysisOrderService;
    private final PatientRepository patientRepository;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        // Get current patient
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        // Get all examination types grouped by category
        List<com.example.LabManagement.Entity.ExaminationCategory> categories = 
            examinationCategoryService.getAllCategories();
        
        model.addAttribute("categories", categories);
        model.addAttribute("examinationTypes", examinationTypeService.getAllTypes());
        model.addAttribute("patient", patient);
        return "patient/analysis-request";
    }

    @PostMapping("/request")
    public String requestAnalysis(@RequestParam Long examinationTypeId, Model model) {
        // Get current patient
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        ExaminationType examType = examinationTypeService.findById(examinationTypeId);
        
        // Create a pending analysis order (without a visit - patient-initiated)
        // Note: This creates an order that needs doctor approval
        AnalysisOrder order = new AnalysisOrder();
        order.setPatient(patient);
        order.setExaminationType(examType);
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(AnalysisStatus.PENDING);
        // Note: doctor and visit will be null for patient-initiated requests
        // These can be assigned later by a doctor
        
        analysisOrderService.createPatientRequestedOrder(order);
        
        return "redirect:/patient/analysis/my-requests?requested";
    }

    @GetMapping("/my-requests")
    public String myRequests(Model model) {
        // Get current patient
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user"));
        
        List<AnalysisOrder> myOrders = analysisOrderService.getOrdersByPatientId(patient.getId());
        
        model.addAttribute("orders", myOrders);
        model.addAttribute("patient", patient);
        return "patient/my-analysis-requests";
    }
}

