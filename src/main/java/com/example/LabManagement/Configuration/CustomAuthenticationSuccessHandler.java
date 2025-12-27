package com.example.LabManagement.Configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        String redirectUrl = "/login";
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            // Spring Security adds ROLE_ prefix automatically
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_DOCTOR")) {
                redirectUrl = "/doctor/dashboard";
                break;
            } else if (role.equals("ROLE_LAB_TECHNICIAN")) {
                redirectUrl = "/labtech/dashboard";
                break;
            } else if (role.equals("ROLE_PATIENT")) {
                redirectUrl = "/patient";
                break;
            }
        }
        
        response.sendRedirect(redirectUrl);
    }
}

