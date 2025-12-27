package com.example.LabManagement.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
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
        }
        return "redirect:/login";
    }
    
//    @GetMapping("/visits")
//    public String visitsRedirect() {
//        return "redirect:/visits/";
//    }
//
//    @GetMapping("/patients")
//    public String patientsRedirect() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            for (GrantedAuthority authority : authorities) {
//                String role = authority.getAuthority();
//                if (role.equals("ROLE_ADMIN")) {
//                    return "redirect:/patients";
//                } else if (role.equals("ROLE_PATIENT")) {
//                    return "redirect:/patient";
//                }
//            }
//        }
//        return "redirect:/login";
//    }
    
    @GetMapping("/doctor")
    public String doctorRedirect() {
        return "redirect:/doctor/dashboard";
    }
}
