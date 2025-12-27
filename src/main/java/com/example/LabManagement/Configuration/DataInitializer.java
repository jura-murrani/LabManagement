package com.example.LabManagement.Configuration;


import com.example.LabManagement.Constants.ExaminationCategories;
import com.example.LabManagement.Entity.Doctor;
import com.example.LabManagement.Entity.ExaminationCategory;
import com.example.LabManagement.Entity.Patient;
import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.DoctorRepository;
import com.example.LabManagement.Repository.ExaminationCategoryRepository;
import com.example.LabManagement.Repository.PatientRepository;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initPatients(PatientRepository patientRepository) {
        return args -> {
            if (patientRepository.count() == 0) {

                return;

            }
            System.out.println("Cant insert");
        };
    }

    @Bean
    CommandLineRunner initUsers(UserAccountRepository userAccountRepository, 
                                 PasswordEncoder passwordEncoder,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository) {
        return args -> {
            if (userAccountRepository.count() == 0) {
                UserAccount admin = new UserAccount(null,
                        "admin",
                        passwordEncoder.encode("a"),
                        Role.ADMIN, true);
                UserAccount doctorUser = new UserAccount(null,
                        "doctor",
                        passwordEncoder.encode("d"),
                        Role.DOCTOR, true);
                UserAccount patientUser = new UserAccount(null,
                        "patient",
                        passwordEncoder.encode("p"),
                        Role.PATIENT, true);
                UserAccount labtech = new UserAccount(null,
                        "labtech",
                        passwordEncoder.encode("l"),
                        Role.LAB_TECHNICIAN, true);
                
                // Save user accounts first
                userAccountRepository.save(admin);
                userAccountRepository.save(doctorUser);
                userAccountRepository.save(labtech);
                userAccountRepository.save(patientUser);
                
                // Create Patient entity for patient user
                Patient patient = new Patient();
                patient.setFirstName("Test");
                patient.setFathersName("Patient");
                patient.setLastName("User");
                patient.setIdNumber("PAT001");
                patient.setBirthDate(LocalDate.of(1990, 1, 1));
                patient.setGender("Male");
                patient.setEmail("patient@test.com");
                patient.setPhone("+1234567890");
                patient.setUserAccount(patientUser);
                patientRepository.save(patient);
                
                // Create Doctor entity for doctor user
                Doctor doctor = new Doctor();
                doctor.setFirstName("Test");
                doctor.setLastName("Doctor");
                doctor.setIdNumber("DOC001");
                doctor.setEmail("doctor@test.com");
                doctor.setPhone("+1234567891");
                doctor.setSpecialization("General Medicine");
                doctor.setLicenseNumber("LIC001");
                doctor.setDepartment(null); // Department is optional
                doctor.setUserAccount(doctorUser);
                doctorRepository.save(doctor);
                
                System.out.println("Inserted user accounts, patient, and doctor successfully");
                return;
            }
            System.out.println("Cant login");
        };
    }

    @Bean
    CommandLineRunner initExaminationCategories(ExaminationCategoryRepository categoryRepository) {
        return args -> {
            // Initialize all predefined categories if they don't exist
            for (String categoryName : ExaminationCategories.ALLOWED_CATEGORIES) {
                if (!categoryRepository.existsByName(categoryName)) {
                    ExaminationCategory category = new ExaminationCategory();
                    category.setName(categoryName);
                    
                    // Set descriptions based on category name
                    if (categoryName.equals(ExaminationCategories.BIOCHEMICAL)) {
                        category.setDescription("Biochemical analysis and tests");
                    } else if (categoryName.equals(ExaminationCategories.HORMONAL)) {
                        category.setDescription("Hormonal analysis and endocrine tests");
                    } else if (categoryName.equals(ExaminationCategories.MICROBIOLOGY)) {
                        category.setDescription("Microbiological analysis and culture tests");
                    } else if (categoryName.equals(ExaminationCategories.HEMATOLOGY)) {
                        category.setDescription("Blood and hematological analysis");
                    } else if (categoryName.equals(ExaminationCategories.IMMUNOLOGY)) {
                        category.setDescription("Immunological tests and antibody analysis");
                    } else if (categoryName.equals(ExaminationCategories.TRANSFUSION_MEDICINE)) {
                        category.setDescription("Blood transfusion and compatibility tests");
                    } else if (categoryName.equals(ExaminationCategories.ANATOMICAL_PATHOLOGY)) {
                        category.setDescription("Tissue and anatomical pathology analysis");
                    } else if (categoryName.equals(ExaminationCategories.MOLECULAR_PATHOLOGY)) {
                        category.setDescription("Molecular diagnostics and genetic testing");
                    } else if (categoryName.equals(ExaminationCategories.TOXICOLOGY)) {
                        category.setDescription("Toxicological analysis and drug screening");
                    } else if (categoryName.equals(ExaminationCategories.URINALYSIS)) {
                        category.setDescription("Urine analysis and related tests");
                    } else if (categoryName.equals(ExaminationCategories.CONSULT)) {
                        category.setDescription("Consultation and review services");
                    } else {
                        category.setDescription("Examination category");
                    }
                    
                    categoryRepository.save(category);
                }
            }
            
            System.out.println("Predefined examination categories initialized. Total categories: " + categoryRepository.count());
        };
    }
}
