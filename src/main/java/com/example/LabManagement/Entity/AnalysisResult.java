package com.example.LabManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Analysis_Results")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resultData;
    private String comment; // Lab tech comment
    private String unit;
    private String referenceRange;
    
    @Column(length = 2000)
    private String doctorNotes; // Doctor's notes after reviewing the result
    
    private Boolean isReadyForPatient = false; // Only true when both lab tech and doctor are finished

    @OneToOne
    @JoinColumn(name = "analysis_order_id")
    private AnalysisOrder analysisOrder;

    @ManyToOne
    @JoinColumn(name = "labtech_id")
    private LabTech labTech;

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultField> resultFields = new ArrayList<>();

}
