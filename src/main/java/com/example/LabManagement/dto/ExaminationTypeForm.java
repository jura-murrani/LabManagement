package com.example.LabManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExaminationTypeForm {
    private Long id;

    @NotBlank(message = "Examination type name is required")
    private String name;

    private String description;
    private String unit;
    private String referenceRange;

    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private String fieldsData; // JSON string containing array of fields with name, unit, referenceRange
}

