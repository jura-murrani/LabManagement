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
@Table(name = "Examination_Type")
public class ExaminationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(length = 2000)
    private String description;
    private String unit;
    private String referenceRange;

    @ManyToOne
    @JoinColumn(name= "category_id")
    private ExaminationCategory examinationCategory;

    @OneToOne(mappedBy = "examinationType", cascade = CascadeType.ALL, orphanRemoval = true)
    private ExaminationTemplate template;
}
