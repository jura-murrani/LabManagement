package com.example.LabManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorForm {
    private Long id;

    @NotBlank(message = "First name of the doctor is required")
    private String firstName;

    @NotBlank(message = "Last name of the doctor is required")
    private String lastName;

    @NotBlank(message = "ID number of the doctor is required")
    private String idNumber;

    @Email(message = "Valid email of the doctor is required")
    private String email;

    @NotBlank(message = "Phone number of the doctor is required")
    private String phone;

    @NotBlank(message = "Medical specialization of the doctor is required")
    private String specialization;

    @NotBlank(message = "License number of the doctor is required")
    private String licenseNumber;

    private Long departmentId;
    
    private String username;
    
    private String password;
}
