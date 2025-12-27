package com.example.LabManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientForm {
    private Long id;

    @NotBlank(message = "First name of the patient is required")
    private String firstName;

    @NotBlank(message = "Father's name of the patient is required")
    private String fathersName;

    @NotBlank(message = "Last name of the patient is required")
    private String lastName;

    @NotBlank(message = "ID number of the patient is required")
    private String idNumber;

    @NotNull(message = "Birth date of the patient is required")
    private LocalDate birthDate;

    @NotBlank(message = "Gender of the patient is required")
    private String gender;

    @Email(message = "Valid email of the patient is required")
    private String email;

    @NotBlank(message = "Phone number of the patient is required")
    private String phone;
    
    private String username;
    
    private String password;
}
