package com.example.LabManagement.Exception;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(String message){
        super(message);
    }
}
