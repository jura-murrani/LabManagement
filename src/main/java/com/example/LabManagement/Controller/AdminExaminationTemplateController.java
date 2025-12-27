package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.ExaminationTemplate;
import com.example.LabManagement.Entity.ExaminationTemplateField;
import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Repository.ExaminationTemplateFieldRepository;
import com.example.LabManagement.Repository.ExaminationTemplateRepository;
import com.example.LabManagement.Repository.ExaminationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/examination-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminExaminationTemplateController {

    private final ExaminationTemplateRepository templateRepository;
    private final ExaminationTemplateFieldRepository fieldRepository;
    private final ExaminationTypeRepository examinationTypeRepository;

    // Template management functionality has been removed
    // All endpoints below are disabled
    
    /*
    @GetMapping("/type/{typeId}")
    public String showTemplateForm(@PathVariable Long typeId, Model model) {
        ExaminationType examinationType = examinationTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Examination type not found"));

        ExaminationTemplate template = templateRepository.findByExaminationTypeId(typeId)
                .orElse(null);

        if (template == null) {
            template = new ExaminationTemplate();
            template.setExaminationType(examinationType);
            template.setFields(new ArrayList<>());
        } else {
            // Load fields ordered by displayOrder
            List<ExaminationTemplateField> fields = fieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
            template.setFields(fields);
        }

        model.addAttribute("template", template);
        model.addAttribute("examinationType", examinationType);
        return "admin/examination-templates/form";
    }

    @PostMapping("/type/{typeId}/save")
    public String saveTemplate(@PathVariable Long typeId,
                              @RequestParam Map<String, String> allParams,
                              RedirectAttributes redirectAttributes) {
        ExaminationType examinationType = examinationTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Examination type not found"));

        ExaminationTemplate template = templateRepository.findByExaminationTypeId(typeId)
                .orElse(new ExaminationTemplate());

        template.setExaminationType(examinationType);

        // Clear existing fields
        if (template.getId() != null) {
            List<ExaminationTemplateField> existingFields = fieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
            fieldRepository.deleteAll(existingFields);
        }
        template.setFields(new ArrayList<>());

        // Extract field data from request parameters
        // Parameters come as: fieldNames[0], fieldNames[1], etc.
        int index = 0;
        while (allParams.containsKey("fieldNames[" + index + "]")) {
            String fieldName = allParams.get("fieldNames[" + index + "]");
            if (fieldName != null && !fieldName.trim().isEmpty()) {
                ExaminationTemplateField field = new ExaminationTemplateField();
                field.setFieldName(fieldName.trim());
                
                String description = allParams.get("descriptions[" + index + "]");
                field.setDescription(description != null ? description.trim() : "");
                
                String unit = allParams.get("units[" + index + "]");
                field.setUnit(unit != null ? unit.trim() : "");
                
                String referenceRange = allParams.get("referenceRanges[" + index + "]");
                field.setReferenceRange(referenceRange != null ? referenceRange.trim() : "");
                
                String displayOrderStr = allParams.get("displayOrders[" + index + "]");
                try {
                    field.setDisplayOrder(displayOrderStr != null && !displayOrderStr.isEmpty() ? Integer.parseInt(displayOrderStr) : index);
                } catch (NumberFormatException e) {
                    field.setDisplayOrder(index);
                }
                
                field.setTemplate(template);
                template.getFields().add(field);
            }
            index++;
        }

        template = templateRepository.save(template);
        
        // Save fields
        for (ExaminationTemplateField field : template.getFields()) {
            fieldRepository.save(field);
        }

        redirectAttributes.addFlashAttribute("success", "Template saved successfully");
        return "redirect:/admin/examination-types?templateSaved";
    }

    @PostMapping("/type/{typeId}/delete")
    public String deleteTemplate(@PathVariable Long typeId, RedirectAttributes redirectAttributes) {
        ExaminationTemplate template = templateRepository.findByExaminationTypeId(typeId)
                .orElse(null);

        if (template != null) {
            templateRepository.delete(template);
            redirectAttributes.addFlashAttribute("success", "Template deleted successfully");
        }

        return "redirect:/admin/examination-types";
    }
    */
}

