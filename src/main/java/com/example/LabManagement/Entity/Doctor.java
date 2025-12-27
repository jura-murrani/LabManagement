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
@Table(name= "Doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String email;
    private String phone;
    private String specialization;
    private String licenseNumber;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
}
