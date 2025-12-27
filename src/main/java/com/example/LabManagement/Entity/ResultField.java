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
@Table(name = "Result_Field")
public class ResultField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName; // Matches ExaminationTemplateField.fieldName
    private String resultValue; // The actual result entered by lab tech
    private String unit; // Copied from template for reference
    private String referenceRange; // Copied from template for reference

    @ManyToOne
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResult analysisResult;
}

