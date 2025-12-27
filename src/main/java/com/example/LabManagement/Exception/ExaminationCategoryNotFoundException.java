package com.example.LabManagement.Exception;

public class ExaminationCategoryNotFoundException extends RuntimeException {
    public ExaminationCategoryNotFoundException(String message) {
        super(message);
    }
}
