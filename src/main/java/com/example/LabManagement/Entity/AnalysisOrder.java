package com.example.LabManagement.Entity;

import com.example.LabManagement.AnalysisStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name= "Analysis_Order")
public class AnalysisOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderedAt;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;
    @ManyToOne
    @JoinColumn(name= "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name= "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "examination_type_id")
    private ExaminationType examinationType;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;
}
