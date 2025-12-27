package com.example.LabManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name= "Patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String fathersName;
    private String lastName;
    private String idNumber;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phone;

    @Transient
    public int getYearsOld() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
}
