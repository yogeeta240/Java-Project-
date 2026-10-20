package com.placement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(jakarta.servlet.http.HttpSession session) {
        if (session != null && session.getAttribute("userRole") != null) {
            String role = (String) session.getAttribute("userRole");
            if ("RECRUITER".equalsIgnoreCase(role)) {
                return "redirect:/recruiter/dashboard";
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                return "redirect:/admin/dashboard";
            } else if ("STUDENT".equalsIgnoreCase(role)) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping({"/design-system", "/design-system.html"})
    public String designSystem() {
        return "design-system";
    }
}
