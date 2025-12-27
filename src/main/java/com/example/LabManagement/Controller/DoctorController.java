package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.AnalysisResult;
import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.AnalysisOrderRepository;
import com.example.LabManagement.Repository.DoctorRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisOrderService;
import com.example.LabManagement.Service.AnalysisResultService;
import com.example.LabManagement.Service.PatientService;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final AnalysisResultService analysisResultService;
    private final PatientService patientService;
    private final AnalysisOrderService analysisOrderService;
    private final AnalysisOrderRepository analysisOrderRepository;
    private final DoctorRepository doctorRepository;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/results")
    public String listResults(Model model, @RequestParam(required = false) Long patientId) {
        List<AnalysisResult> results;
        if (patientId != null) {
            results = analysisResultService.getResultsByPatientId(patientId);
            model.addAttribute("selectedPatient", patientService.findPatientById(patientId));
        } else {
            results = analysisResultService.getAllResults();
        }
        
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
        model.addAttribute("patients", patientService.getAllPatients());
        return "doctor/results";
    }

    @GetMapping("/results/{id}")
    public String viewResult(@PathVariable Long id, Model model) {
        AnalysisResult result = analysisResultService.getResultById(id);
        model.addAttribute("result", result);
        return "doctor/result-view";
    }

    @PostMapping("/results/{id}/notes")
    public String addDoctorNotes(@PathVariable Long id, @RequestParam String doctorNotes) {
        analysisResultService.addDoctorNotes(id, doctorNotes);
        return "redirect:/doctor/results/" + id + "?notesAdded";
    }
    
    @GetMapping("/patient-requests")
    public String viewPatientRequests(Model model) {
        // Get pending patient-requested orders (without doctor assignment)
        List<AnalysisOrder> pendingRequests = analysisOrderService.getPendingPatientRequests();
        model.addAttribute("pendingRequests", pendingRequests);
        return "doctor/patient-requests";
    }
    
    @PostMapping("/patient-requests/{id}/approve")
    public String approvePatientRequest(@PathVariable Long id) {
        // Get current doctor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Doctor doctor = doctorRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for user"));
        
        // Assign doctor to the order
        AnalysisOrder order = analysisOrderService.findOrderById(id);
        if (order.getDoctor() != null) {
            return "redirect:/doctor/patient-requests?alreadyAssigned";
        }
        order.setDoctor(doctor);
        analysisOrderRepository.save(order);
        
        return "redirect:/doctor/patient-requests?approved";
    }
    
    @PostMapping("/patient-requests/{id}/reject")
    public String rejectPatientRequest(@PathVariable Long id) {
        AnalysisOrder order = analysisOrderService.findOrderById(id);
        order.setStatus(com.example.LabManagement.AnalysisStatus.CANCELLED);
        analysisOrderRepository.save(order);
        return "redirect:/doctor/patient-requests?rejected";
    }
}

