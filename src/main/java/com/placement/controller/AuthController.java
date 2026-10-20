package com.placement.controller;

import com.placement.model.*;
import com.placement.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;

    public AuthController(UserRepository userRepository,
                          StudentProfileRepository studentProfileRepository,
                          RecruiterProfileRepository recruiterProfileRepository,
                          CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping({"/login", "/auth/login", "/login.html", "/auth/login.html"})
    public String loginPage(@RequestParam(required = false) String registered,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String error,
                            @RequestParam(required = false) String email,
                            Model model) {
        model.addAttribute("registered", registered != null);
        model.addAttribute("loggedOut", logout != null);
        model.addAttribute("hasError", error != null);
        model.addAttribute("registeredEmail", email != null ? email : "");
        return "auth/login";
    }

    @PostMapping({"/login", "/auth/login"})
    public String handleLogin(@RequestParam(required = false) String email,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String password,
                              @RequestParam(required = false) String role,
                              HttpSession session) {
        String effectiveEmail = (email != null && !email.isBlank()) ? email : username;

        // Find user by email or full name or persona role fallback
        Optional<User> userOpt = Optional.empty();
        if (effectiveEmail != null && !effectiveEmail.isBlank()) {
            String term = effectiveEmail.trim().toLowerCase();
            userOpt = userRepository.findByEmail(term);
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findAll().stream()
                        .filter(u -> (u.getFullName() != null && u.getFullName().trim().equalsIgnoreCase(effectiveEmail.trim())) ||
                                     (u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(term)))
                        .findFirst();
            }
        }

        if (userOpt.isEmpty() && role != null && !role.isBlank()) {
            try {
                Role r = Role.valueOf(role.toUpperCase().trim());
                userOpt = userRepository.findByRole(r).stream().findFirst();
            } catch (Exception ignored) {}
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userRole", user.getRole().name());

            if (user.getRole() == Role.RECRUITER) {
                boolean hasProfile = recruiterProfileRepository.findByUser(user).isPresent() ||
                        recruiterProfileRepository.findAll().stream().anyMatch(p -> p.getUser() != null && (user.getId().equals(p.getUser().getId()) || user.getEmail().equalsIgnoreCase(p.getUser().getEmail())));
                if (!hasProfile) {
                    String orgName = (user.getFullName() != null && !user.getFullName().isBlank()) ? (user.getFullName().trim() + " Enterprise") : "Partner Organization";
                    Company company = companyRepository.findByName(orgName).orElseGet(() ->
                            companyRepository.save(new Company(orgName, "Technology", "https://" + orgName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com", "Pan-India", "Campus hiring partner enterprise."))
                    );
                    recruiterProfileRepository.save(new RecruiterProfile(user, company, "Campus Talent Acquisition Partner", "+91 99887 00000"));
                }
                return "redirect:/recruiter/dashboard";
            } else if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        }

        return "redirect:/login?error=true";
    }

    @GetMapping({"/register", "/auth/register", "/register.html", "/auth/register.html"})
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping({"/register", "/auth/register"})
    public String handleRegister(@RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String role,
                                 @RequestParam(required = false) String department,
                                 @RequestParam(required = false) String rollNumber,
                                 @RequestParam(required = false) Double cgpa,
                                 @RequestParam(required = false) Integer batchYear,
                                 @RequestParam(required = false) String companyName,
                                 @RequestParam(required = false) String recruiterDesignation,
                                 @RequestParam(required = false) String companyWebsite,
                                 @RequestParam(required = false) String companyLocation,
                                 @RequestParam(required = false) String adminDesignation,
                                 @RequestParam(required = false) String employeeId,
                                 @RequestParam(required = false) String officeDepartment,
                                 @RequestParam(required = false) String phone) {
        Role userRole = Role.STUDENT;
        try {
            userRole = Role.valueOf(role.toUpperCase().trim());
        } catch (Exception ignored) {}

        if (userRepository.findByEmail(email.toLowerCase().trim()).isPresent()) {
            return "redirect:/register?error=exists";
        }

        User user = new User(email.toLowerCase().trim(), password, fullName.trim(), userRole);
        user = userRepository.save(user);

        if (userRole == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            String roll = (rollNumber != null && !rollNumber.isBlank()) ? rollNumber.trim() : ("2026-" + user.getId().substring(Math.max(0, user.getId().length() - 4)));
            profile.setRollNumber(roll);
            profile.setDepartment((department != null && !department.isBlank()) ? department.trim() : "Computer Science & Engineering");
            profile.setCgpa(cgpa != null ? cgpa : 8.0);
            profile.setBatchYear(batchYear != null ? batchYear : 2026);
            profile.setActiveBacklogs(0);
            profile.setSkills("Java, Python, SQL");
            if (phone != null && !phone.isBlank()) profile.setPhone(phone.trim());
            studentProfileRepository.save(profile);
        } else if (userRole == Role.RECRUITER) {
            String comp = (companyName != null && !companyName.isBlank()) ? companyName.trim() : (fullName.trim() + " Enterprise");
            String web = (companyWebsite != null && !companyWebsite.isBlank()) ? companyWebsite.trim() : ("https://" + comp.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com");
            String loc = (companyLocation != null && !companyLocation.isBlank()) ? companyLocation.trim() : "Pan-India";
            String desig = (recruiterDesignation != null && !recruiterDesignation.isBlank()) ? recruiterDesignation.trim() : "Campus Talent Acquisition Partner";
            String ph = (phone != null && !phone.isBlank()) ? phone.trim() : "+91 99887 00000";

            boolean isDemoRecruiter = "recruiter@microsoft.com".equalsIgnoreCase(email) || "recruiter@google.com".equalsIgnoreCase(email);
            Company company;
            if (!isDemoRecruiter && ("Microsoft Corporation".equalsIgnoreCase(comp) || "Microsoft".equalsIgnoreCase(comp) || "Google".equalsIgnoreCase(comp))) {
                String uniqueComp = comp + " (" + fullName.trim() + ")";
                company = companyRepository.findByName(uniqueComp).orElseGet(() ->
                        companyRepository.save(new Company(uniqueComp, "Technology", web, loc, "Campus hiring partner enterprise."))
                );
            } else {
                company = companyRepository.findByName(comp).orElseGet(() ->
                        companyRepository.save(new Company(comp, "Technology", web, loc, "Campus hiring partner organization."))
                );
            }
            RecruiterProfile profile = new RecruiterProfile(user, company, desig, ph);
            recruiterProfileRepository.save(profile);
        }

        return "redirect:/login?registered=true&email=" + java.net.URLEncoder.encode(user.getEmail(), java.nio.charset.StandardCharsets.UTF_8);
    }

    @GetMapping({"/logout", "/auth/logout"})
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
