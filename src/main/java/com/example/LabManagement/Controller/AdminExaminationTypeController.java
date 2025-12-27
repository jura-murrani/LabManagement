package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Entity.ExaminationTemplate;
import com.example.LabManagement.Entity.ExaminationTemplateField;
import com.example.LabManagement.Repository.ExaminationTemplateRepository;
import com.example.LabManagement.Repository.ExaminationTemplateFieldRepository;
import com.example.LabManagement.Service.ExaminationCategoryService;
import com.example.LabManagement.Service.ExaminationTypeService;
import com.example.LabManagement.dto.ExaminationTypeForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/examination-types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminExaminationTypeController {

    private final ExaminationTypeService examinationTypeService;
    private final ExaminationCategoryService examinationCategoryService;
    private final ExaminationTemplateRepository templateRepository;
    private final ExaminationTemplateFieldRepository fieldRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String listExaminationTypes(Model model) {
        List<ExaminationType> types = examinationTypeService.getAllTypes();
        model.addAttribute("examinationTypes", types);
        return "admin/examination-types/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("examinationTypeForm", new ExaminationTypeForm());
        model.addAttribute("categories", examinationCategoryService.getAllCategories());
        model.addAttribute("fieldsData", "[]");
        return "admin/examination-types/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ExaminationType type = examinationTypeService.findById(id);
        ExaminationTypeForm form = new ExaminationTypeForm();
        form.setId(type.getId());
        form.setName(type.getName());
        form.setDescription(type.getDescription());
        form.setUnit(type.getUnit());
        form.setReferenceRange(type.getReferenceRange());
        if (type.getExaminationCategory() != null) {
            form.setCategoryId(type.getExaminationCategory().getId());
        }
        
        // Load template fields if they exist
        String fieldsData = "[]";
        ExaminationTemplate template = templateRepository.findByExaminationTypeId(id).orElse(null);
        if (template != null && template.getId() != null) {
            List<ExaminationTemplateField> fields = fieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
            if (fields != null && !fields.isEmpty()) {
                List<Map<String, String>> fieldsList = new ArrayList<>();
                for (ExaminationTemplateField field : fields) {
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("name", field.getFieldName() != null ? field.getFieldName() : "");
                    fieldMap.put("unit", field.getUnit() != null ? field.getUnit() : "");
                    fieldMap.put("referenceRange", field.getReferenceRange() != null ? field.getReferenceRange() : "");
                    fieldsList.add(fieldMap);
                }
                try {
                    fieldsData = objectMapper.writeValueAsString(fieldsList);
                } catch (Exception e) {
                    e.printStackTrace();
                    fieldsData = "[]";
                }
            }
        }
        
        model.addAttribute("examinationTypeForm", form);
        model.addAttribute("categories", examinationCategoryService.getAllCategories());
        model.addAttribute("fieldsData", fieldsData);
        return "admin/examination-types/form";
    }

    @PostMapping("/save")
    public String saveExaminationType(@ModelAttribute("examinationTypeForm") @Valid ExaminationTypeForm form,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", examinationCategoryService.getAllCategories());
            return "admin/examination-types/form";
        }

        try {
            ExaminationType type = new ExaminationType();
            type.setName(form.getName());
            type.setDescription(form.getDescription());
            type.setUnit(form.getUnit());
            type.setReferenceRange(form.getReferenceRange());

            ExaminationType savedType;
            if (form.getId() != null) {
                // Update existing
                ExaminationType existing = examinationTypeService.findById(form.getId());
                existing.setName(form.getName());
                existing.setDescription(form.getDescription());
                existing.setUnit(form.getUnit());
                existing.setReferenceRange(form.getReferenceRange());
                if (form.getCategoryId() != null) {
                    existing.setExaminationCategory(examinationCategoryService.findById(form.getCategoryId()));
                }
                savedType = examinationTypeService.updateType(form.getId(), existing);
            } else {
                // Create new
                savedType = examinationTypeService.createType(type, form.getCategoryId());
            }
            
            // Save template fields if provided
            if (form.getFieldsData() != null && !form.getFieldsData().trim().isEmpty()) {
                try {
                    List<Map<String, String>> fieldsList = objectMapper.readValue(
                        form.getFieldsData(), 
                        new TypeReference<List<Map<String, String>>>() {}
                    );
                    
                    // Get or create template
                    ExaminationTemplate template = templateRepository.findByExaminationTypeId(savedType.getId())
                        .orElse(new ExaminationTemplate());
                    template.setExaminationType(savedType);
                    
                    // Delete existing fields before saving new template
                    if (template.getId() != null) {
                        List<ExaminationTemplateField> existingFields = 
                            fieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
                        fieldRepository.deleteAll(existingFields);
                    }
                    
                    template = templateRepository.save(template);
                    
                    // Save new fields (even if empty list, this clears all fields)
                    if (fieldsList != null && !fieldsList.isEmpty()) {
                        for (int i = 0; i < fieldsList.size(); i++) {
                            Map<String, String> fieldData = fieldsList.get(i);
                            String name = fieldData.get("name");
                            if (name != null && !name.trim().isEmpty()) {
                                ExaminationTemplateField field = new ExaminationTemplateField();
                                field.setFieldName(name.trim());
                                field.setUnit(fieldData.getOrDefault("unit", "").trim());
                                field.setReferenceRange(fieldData.getOrDefault("referenceRange", "").trim());
                                field.setDisplayOrder(i);
                                field.setTemplate(template);
                                fieldRepository.save(field);
                            }
                        }
                    }
                } catch (Exception e) {
                    // If JSON parsing fails, just continue without saving fields
                    e.printStackTrace();
                }
            }
            
            return "redirect:/admin/examination-types?success";
        } catch (Exception ex) {
            bindingResult.rejectValue("name", "examinationTypeForm.name", ex.getMessage());
            model.addAttribute("categories", examinationCategoryService.getAllCategories());
            return "admin/examination-types/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteExaminationType(@PathVariable Long id) {
        examinationTypeService.deleteType(id);
        return "redirect:/admin/examination-types?deleted";
    }
}

