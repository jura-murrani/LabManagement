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
@Table(name = "Lab_Tech")
public class LabTech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String qualification;
    private String idNumber;
    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
}
