package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

@Controller
@RequestMapping("/change-password")
@RequiredArgsConstructor
public class PasswordChangeController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showChangePasswordForm(Model model) {
        return "change-password";
    }

    @PostMapping
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, userAccount.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/change-password";
        }
        
        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match");
            return "redirect:/change-password";
        }
        
        // Check if new password is not empty
        if (newPassword == null || newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "New password cannot be empty");
            return "redirect:/change-password";
        }
        
        // Update password
        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(userAccount);
        
        redirectAttributes.addFlashAttribute("success", "Password changed successfully. Please login with your new password.");
        
        // Redirect to appropriate dashboard based on role
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_DOCTOR")) {
                return "redirect:/doctor/dashboard";
            } else if (role.equals("ROLE_LAB_TECHNICIAN")) {
                return "redirect:/labtech/dashboard";
            } else if (role.equals("ROLE_PATIENT")) {
                return "redirect:/patient";
            }
        }
        
        return "redirect:/change-password";
    }
}

