package com.example.LabManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Examination_Template_Field")
public class ExaminationTemplateField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName; // e.g., "FBS", "Insulin", "IR"
    
    @Column(length = 1000)
    private String description; // e.g., "Fasting Blood Sugar (FBS or Fasting Glucose)"
    
    private String unit; // e.g., "mg/dL", "µIU/mL", "Index"
    
    private String referenceRange; // e.g., "70-100", "Fasting: 2.6 - 10.0 Random: 2.6 - 24.9", "< 1.8"
    
    private Integer displayOrder; // Order in which fields appear in the form

    @ManyToOne
    @JoinColumn(name = "template_id")
    private ExaminationTemplate template;
}

