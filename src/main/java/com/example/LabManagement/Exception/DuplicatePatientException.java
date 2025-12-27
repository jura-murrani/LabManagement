package com.example.LabManagement.Exception;

public class DuplicatePatientException extends RuntimeException {
    public DuplicatePatientException(String message){
        super(message);
    }
}
