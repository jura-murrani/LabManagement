package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.ExaminationTemplate;
import com.example.LabManagement.Entity.ExaminationTemplateField;
import com.example.LabManagement.Entity.LabTech;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.ExaminationTemplateFieldRepository;
import com.example.LabManagement.Repository.ExaminationTemplateRepository;
import com.example.LabManagement.Repository.LabTechRepository;
import com.example.LabManagement.Repository.ResultFieldRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Service.AnalysisOrderService;
import com.example.LabManagement.Service.AnalysisResultService;
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
@RequestMapping("/labtech")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LAB_TECHNICIAN')")
public class LabTechController {

    private final AnalysisOrderService analysisOrderService;
    private final AnalysisResultService analysisResultService;
    private final LabTechRepository labTechRepository;
    private final UserAccountRepository userAccountRepository;
    private final ExaminationTemplateRepository templateRepository;
    private final ExaminationTemplateFieldRepository templateFieldRepository;
    private final ResultFieldRepository resultFieldRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get current lab tech
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LabTech labTech = labTechRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("LabTech not found for user"));
        
        // Get all orders (not just pending) to show status of every analysis
        List<AnalysisOrder> allOrders = analysisOrderService.getAllOrders();
        
        // Group orders by patient, filtering out orders with null patients
        Map<com.example.LabManagement.Entity.Patient, List<AnalysisOrder>> ordersByPatient = 
            allOrders.stream()
                .filter(order -> order.getPatient() != null)
                .collect(Collectors.groupingBy(AnalysisOrder::getPatient));
        
        List<AnalysisOrder> pendingOrders = analysisOrderService.getPendingOrders();
        
        model.addAttribute("ordersByPatient", ordersByPatient);
        model.addAttribute("totalOrders", pendingOrders.size());
        model.addAttribute("labTech", labTech);
        model.addAttribute("allOrders", allOrders);
        return "labtech/dashboard";
    }

    @GetMapping("/orders")
    public String listPendingOrders(@RequestParam(required = false) String search, Model model) {
        // Get current lab tech
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LabTech labTech = labTechRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("LabTech not found for user"));
        
        // Get pending and in-progress orders (not completed)
        List<AnalysisOrder> notCompletedOrders = analysisOrderService.getAllOrders().stream()
                .filter(order -> order.getStatus() != com.example.LabManagement.AnalysisStatus.COMPLETED 
                        && order.getStatus() != com.example.LabManagement.AnalysisStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());
        
        // Get completed orders
        List<AnalysisOrder> completedOrders = analysisOrderService.getAllOrders().stream()
                .filter(order -> order.getStatus() == com.example.LabManagement.AnalysisStatus.COMPLETED)
                .collect(java.util.stream.Collectors.toList());
        
        // Apply search filter if provided
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase().trim();
            notCompletedOrders = notCompletedOrders.stream()
                    .filter(order -> order.getPatient() != null && 
                            (order.getPatient().getFirstName() != null && order.getPatient().getFirstName().toLowerCase().contains(searchLower) ||
                             order.getPatient().getLastName() != null && order.getPatient().getLastName().toLowerCase().contains(searchLower)))
                    .collect(java.util.stream.Collectors.toList());
            
            completedOrders = completedOrders.stream()
                    .filter(order -> order.getPatient() != null && 
                            (order.getPatient().getFirstName() != null && order.getPatient().getFirstName().toLowerCase().contains(searchLower) ||
                             order.getPatient().getLastName() != null && order.getPatient().getLastName().toLowerCase().contains(searchLower)))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Create a map to check which orders have results (for preview)
        java.util.Map<Long, Boolean> hasResult = new java.util.HashMap<>();
        // Create a map of order ID to result for displaying results
        java.util.Map<Long, com.example.LabManagement.Entity.AnalysisResult> orderResults = new java.util.HashMap<>();
        for (AnalysisOrder order : completedOrders) {
            com.example.LabManagement.Entity.AnalysisResult result = analysisResultService.getResultByOrderOrNull(order.getId());
            hasResult.put(order.getId(), result != null);
            if (result != null) {
                // Load result fields
                result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
                orderResults.put(order.getId(), result);
            }
        }
        
        model.addAttribute("notCompletedOrders", notCompletedOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("hasResult", hasResult);
        model.addAttribute("orderResults", orderResults);
        model.addAttribute("labTech", labTech);
        return "labtech/orders";
    }

    @GetMapping("/orders/{id}/result")
    public String showResultForm(@PathVariable Long id, Model model) {
        AnalysisOrder order = analysisOrderService.findOrderById(id);
        model.addAttribute("order", order);
        
        // Get current lab tech
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LabTech labTech = labTechRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("LabTech not found for user"));
        model.addAttribute("labTech", labTech);
        
        // Load template if exists
        ExaminationTemplate template = templateRepository.findByExaminationTypeId(order.getExaminationType().getId())
                .orElse(null);
        
        if (template != null) {
            // Load fields ordered by displayOrder
            List<ExaminationTemplateField> fields = templateFieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
            template.setFields(fields);
            
            if (fields != null && !fields.isEmpty()) {
                model.addAttribute("template", template);
                model.addAttribute("hasTemplate", true);
            } else {
                model.addAttribute("hasTemplate", false);
            }
        } else {
            model.addAttribute("hasTemplate", false);
        }
        
        return "labtech/result-form";
    }

    @PostMapping("/orders/{id}/result")
    public String submitResult(@PathVariable Long id,
                              @RequestParam(required = false) String resultData,
                              @RequestParam(required = false) String comment,
                              @RequestParam(required = false) String unit,
                              @RequestParam(required = false) String referenceRange,
                              @RequestParam Map<String, String> allParams) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LabTech labTech = labTechRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("LabTech not found for user"));
        
        AnalysisOrder order = analysisOrderService.findOrderById(id);
        ExaminationTemplate template = templateRepository.findByExaminationTypeId(order.getExaminationType().getId())
                .orElse(null);
        
        // Check if template exists and has fields
        if (template != null) {
            List<ExaminationTemplateField> fields = templateFieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
            if (fields != null && !fields.isEmpty()) {
                // Use template-based submission
                Map<String, String> fieldResults = new java.util.HashMap<>();
                Map<String, String> fieldUnits = new java.util.HashMap<>();
                Map<String, String> fieldReferenceRanges = new java.util.HashMap<>();
                for (ExaminationTemplateField field : fields) {
                    String fieldValue = allParams.get("field_" + field.getFieldName());
                    if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                        fieldResults.put(field.getFieldName(), fieldValue);
                        fieldUnits.put(field.getFieldName(), field.getUnit() != null ? field.getUnit() : "");
                        fieldReferenceRanges.put(field.getFieldName(), field.getReferenceRange() != null ? field.getReferenceRange() : "");
                    }
                }
                if (!fieldResults.isEmpty()) {
                    analysisResultService.createResultWithFields(id, labTech.getId(), fieldResults, comment, fieldUnits, fieldReferenceRanges);
                } else {
                    throw new RuntimeException("At least one result field must be filled");
                }
            } else {
                // Use legacy single-field submission
                if (resultData == null || resultData.trim().isEmpty()) {
                    throw new RuntimeException("Result data is required");
                }
                analysisResultService.createResult(id, labTech.getId(), resultData, comment, unit, referenceRange);
            }
        } else {
            // Use legacy single-field submission
            if (resultData == null || resultData.trim().isEmpty()) {
                throw new RuntimeException("Result data is required");
            }
            analysisResultService.createResult(id, labTech.getId(), resultData, comment, unit, referenceRange);
        }
        
        return "redirect:/labtech/orders?submitted";
    }
    
    @GetMapping("/orders/{id}/preview")
    public String previewResult(@PathVariable Long id, Model model) {
        AnalysisOrder order = analysisOrderService.findOrderById(id);
        model.addAttribute("order", order);
        
        // Get result if exists
        com.example.LabManagement.Entity.AnalysisResult result = analysisResultService.getResultByOrderOrNull(id);
        model.addAttribute("result", result);
        
        // Get current lab tech
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LabTech labTech = labTechRepository.findByUserAccountId(userAccount.getId())
                .orElseThrow(() -> new RuntimeException("LabTech not found for user"));
        model.addAttribute("labTech", labTech);
        
        // Load template if exists
        ExaminationTemplate template = templateRepository.findByExaminationTypeId(order.getExaminationType().getId())
                .orElse(null);
        
        // Load result fields if result exists
        if (result != null) {
            result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
            
            // If result fields exist but some have empty reference ranges, backfill from template
            if (result.getResultFields() != null && !result.getResultFields().isEmpty() && template != null) {
                List<ExaminationTemplateField> templateFields = templateFieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
                // Create a map of field name to template field for quick lookup
                java.util.Map<String, ExaminationTemplateField> templateFieldMap = new java.util.HashMap<>();
                for (ExaminationTemplateField tf : templateFields) {
                    templateFieldMap.put(tf.getFieldName(), tf);
                }
                
                // Backfill missing reference ranges and units from template
                for (com.example.LabManagement.Entity.ResultField rf : result.getResultFields()) {
                    ExaminationTemplateField tf = templateFieldMap.get(rf.getFieldName());
                    if (tf != null) {
                        if (rf.getReferenceRange() == null || rf.getReferenceRange().trim().isEmpty()) {
                            rf.setReferenceRange(tf.getReferenceRange() != null ? tf.getReferenceRange() : "");
                        }
                        if (rf.getUnit() == null || rf.getUnit().trim().isEmpty()) {
                            rf.setUnit(tf.getUnit() != null ? tf.getUnit() : "");
                        }
                    }
                }
                
                model.addAttribute("hasTemplate", true);
            } else if (result.getResultFields() != null && !result.getResultFields().isEmpty()) {
                model.addAttribute("hasTemplate", true);
            } else {
                model.addAttribute("hasTemplate", false);
            }
        } else {
            model.addAttribute("hasTemplate", false);
        }
        
        return "labtech/result-preview";
    }
}

