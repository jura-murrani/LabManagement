package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {
    private final UserAccountRepository repository;
    public UserAccountDetailsService(UserAccountRepository repository){
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));


        // Convert enum to string and handle LAB_TECHNICIAN properly
        String roleName = userAccount.getRole().name();
        return User.withUsername(userAccount.getUsername())
                .password(userAccount.getPassword())
                .roles(roleName)
                .build();


    }
}
