package com.example.LabManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabTechForm {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "ID number is required")
    private String idNumber;

    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String qualification;

    private Long departmentId;
    
    private String username;
    
    private String password;
}

