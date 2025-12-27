package com.example.LabManagement.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Predefined examination categories that are allowed in the system.
 * Users can only select from these categories when creating examination types.
 */
public class ExaminationCategories {
    
    public static final String BIOCHEMICAL = "Biochemical";
    public static final String HORMONAL = "Hormonal";
    public static final String MICROBIOLOGY = "Microbiology";
    public static final String HEMATOLOGY = "Hematology";
    public static final String IMMUNOLOGY = "Immunology";
    public static final String TRANSFUSION_MEDICINE = "Transfusion Medicine";
    public static final String ANATOMICAL_PATHOLOGY = "Anatomical Pathology";
    public static final String MOLECULAR_PATHOLOGY = "Molecular Pathology/Diagnostics";
    public static final String TOXICOLOGY = "Toxicology";
    public static final String URINALYSIS = "Urinalysis";
    public static final String CONSULT = "Consult";
    
    /**
     * List of all allowed category names
     */
    public static final List<String> ALLOWED_CATEGORIES = Arrays.asList(
        BIOCHEMICAL,
        HORMONAL,
        MICROBIOLOGY,
        HEMATOLOGY,
        IMMUNOLOGY,
        TRANSFUSION_MEDICINE,
        ANATOMICAL_PATHOLOGY,
        MOLECULAR_PATHOLOGY,
        TOXICOLOGY,
        URINALYSIS,
        CONSULT
    );
    
    /**
     * Check if a category name is allowed
     */
    public static boolean isAllowed(String categoryName) {
        if (categoryName == null) {
            return false;
        }
        return ALLOWED_CATEGORIES.contains(categoryName.trim());
    }
    
    private ExaminationCategories() {
        // Utility class - prevent instantiation
    }
}

