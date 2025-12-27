package com.example.LabManagement.Controller;

import com.example.LabManagement.AnalysisStatus;
import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Repository.AnalysisResultRepository;
import com.example.LabManagement.Service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final VisitService visitService;
    private final AnalysisResultRepository analysisResultRepository;

    @GetMapping
    public String dashboard(Model model, @RequestParam(required = false) LocalDate date) {
        // Default to today if no date specified
        final LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        
        // Get all visits for the selected date
        List<Visit> allVisits = visitService.getAllVisits();
        List<Visit> dailyVisits = allVisits.stream()
                .filter(v -> v.getVisitDate() != null && 
                        v.getVisitDate().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());
        
        // Create maps for labtech names and status counts per visit
        Map<Long, Set<String>> visitLabTechs = new HashMap<>();
        Map<Long, Map<AnalysisStatus, Long>> visitStatusCounts = new HashMap<>();
        
        for (Visit visit : dailyVisits) {
            Set<String> labTechNames = new HashSet<>();
            Map<AnalysisStatus, Long> statusCounts = new HashMap<>();
            statusCounts.put(AnalysisStatus.PENDING, 0L);
            statusCounts.put(AnalysisStatus.IN_PROGRESS, 0L);
            statusCounts.put(AnalysisStatus.COMPLETED, 0L);
            statusCounts.put(AnalysisStatus.CANCELLED, 0L);
            
            if (visit.getAnalysisOrders() != null) {
                for (AnalysisOrder order : visit.getAnalysisOrders()) {
                    // Count statuses
                    AnalysisStatus status = order.getStatus();
                    statusCounts.put(status, statusCounts.get(status) + 1);
                    
                    // Get labtech from result
                    analysisResultRepository.findByAnalysisOrderId(order.getId())
                            .ifPresent(result -> {
                                if (result.getLabTech() != null) {
                                    labTechNames.add(result.getLabTech().getFirstName() + " " + result.getLabTech().getLastName());
                                }
                            });
                }
            }
            
            visitLabTechs.put(visit.getId(), labTechNames);
            visitStatusCounts.put(visit.getId(), statusCounts);
        }
        
        model.addAttribute("visits", dailyVisits);
        model.addAttribute("visitLabTechs", visitLabTechs);
        model.addAttribute("visitStatusCounts", visitStatusCounts);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("today", LocalDate.now());
        return "admin/dashboard";
    }
}

