package com.example.LabManagement.Exception;

public class DuplicateDepartmentException extends RuntimeException {
    public DuplicateDepartmentException(String message) {
        super(message);
    }
}
