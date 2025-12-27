package com.example.LabManagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @NotBlank(message = "Username is requiered")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Role is required")
    private String role;
}
