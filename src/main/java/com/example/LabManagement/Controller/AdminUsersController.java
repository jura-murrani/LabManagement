package com.example.LabManagement.Controller;

import com.example.LabManagement.Entity.UserAccount;
import com.example.LabManagement.Service.UserAccountService;
import com.example.LabManagement.dto.UserForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsersController {
    private final UserAccountService userAccountService;

    public AdminUsersController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;

    }

    @ModelAttribute("userForm")
    public UserForm userForm() {
        return new UserForm();
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userAccountService.findAll());
        return "admin/users";
    }

    @PostMapping
    public String createUser(@ModelAttribute("userForm") @Valid UserForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                userAccountService.createUser(form);
                return "redirect:/admin/users?created";
            } catch (IllegalArgumentException ex) {
                bindingResult.rejectValue("username", "userForm.username", ex.getMessage());
            }
        }

        model.addAttribute("users", userAccountService.findAll());
        model.addAttribute("userForm", new UserAccount());
        model.addAttribute("error", true);
        return "admin/users";
    }
}
