package com.example.LabManagement.Service;


import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import com.example.LabManagement.Role;
import com.example.LabManagement.dto.UserForm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAccountService {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserAccount> findAll() {
        return repository.findAll();
    }

    @Transactional
    public UserAccount createUser(UserForm dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        String normalizedRole = normalizeRole(dto.getRole());

        repository.findByUsername(dto.getUsername().trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Username already exists");
                });
        try {
            UserAccount user = new UserAccount();
            user.setUsername(dto.getUsername().trim());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(Role.valueOf(normalizedRole));
            return repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Unable to save user");
        }
    }
    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        String value = role.trim().toUpperCase();
        return switch (value) {
            case "ADMIN", "USER" -> value;
            default -> "USER";
        };
    }
}
