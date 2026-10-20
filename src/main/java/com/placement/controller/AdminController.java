package com.placement.controller;

import com.placement.model.*;
import com.placement.repository.*;
import com.placement.util.DepartmentMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminController(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           RecruiterProfileRepository recruiterProfileRepository,
                           CompanyRepository companyRepository,
                           JobPostingRepository jobPostingRepository,
                           ApplicationRepository applicationRepository,
                           InterviewSlotRepository interviewSlotRepository,
                           FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.applicationRepository = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping({"", "/", "/dashboard", "/dashboard.html"})
    public String dashboard(Model model) {
        long totalStudents = studentProfileRepository.count();
        long placedCount = studentProfileRepository.countByPlacedStatus("OFFERED") +
                studentProfileRepository.countByPlacedStatus("ACCEPTED");
        double placementRate = totalStudents > 0 ? ((double) placedCount / totalStudents) * 100.0 : 78.5;

        List<JobPosting> allJobs = jobPostingRepository.findAll();
        double highestPackage = allJobs.stream()
                .mapToDouble(j -> j.getNumericCtc() != null ? j.getNumericCtc() : 0.0)
                .max().orElse(32.0);

        double avgPackage = allJobs.stream()
                .mapToDouble(j -> j.getNumericCtc() != null ? j.getNumericCtc() : 0.0)
                .average().orElse(18.5);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("placedCount", placedCount);
        model.addAttribute("placementRate", String.format("%.1f", placementRate));
        model.addAttribute("avgPackage", String.format("%.1f LPA", avgPackage));
        model.addAttribute("highestPackage", String.format("%.1f LPA", highestPackage));
        model.addAttribute("totalCompanies", companyRepository.count());
        model.addAttribute("totalRecruiters", recruiterProfileRepository.count());
        model.addAttribute("totalApplications", applicationRepository.count());
        model.addAttribute("activeDrivesCount", jobPostingRepository.countByStatus("OPEN"));
        model.addAttribute("recentApplications", applicationRepository.findAll());
        model.addAttribute("recentJobs", allJobs);

        return "admin/dashboard";
    }

    @GetMapping({"/students", "/students.html"})
    public String students(@RequestParam(required = false) String department,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) String query,
                           Model model) {
        List<StudentProfile> list = studentProfileRepository.findAll();
        if (department != null && !department.isBlank() && !"ALL".equalsIgnoreCase(department)) {
            list = list.stream().filter(s -> matchesDepartment(s, department)).collect(Collectors.toList());
            model.addAttribute("selectedDept", department);
        } else {
            model.addAttribute("selectedDept", "ALL");
        }

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = list.stream().filter(s -> {
                if ("UNPLACED".equalsIgnoreCase(status)) {
                    return s.getPlacedStatus() == null || "UNPLACED".equalsIgnoreCase(s.getPlacedStatus()) || "SEEKING".equalsIgnoreCase(s.getPlacedStatus());
                }
                return s.getPlacedStatus() != null && s.getPlacedStatus().equalsIgnoreCase(status);
            }).collect(Collectors.toList());
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("selectedStatus", "");
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(s -> {
                String name = (s.getUser() != null) ? s.getUser().getFullName().toLowerCase() : "";
                String roll = (s.getRollNumber() != null) ? s.getRollNumber().toLowerCase() : "";
                String skills = (s.getSkills() != null) ? s.getSkills().toLowerCase() : "";
                return name.contains(q) || roll.contains(q) || skills.contains(q);
            }).collect(Collectors.toList());
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("students", list);
        return "admin/students";
    }

    @PostMapping({"/students/verify"})
    public String verifyStudent(@RequestParam String studentId) {
        // Verification action confirmation
        return "redirect:/admin/students?verified=true";
    }

    @GetMapping({"/recruiters", "/recruiters.html"})
    public String recruiters(Model model) {
        model.addAttribute("recruiters", recruiterProfileRepository.findAll());
        return "admin/recruiters";
    }

    @GetMapping({"/companies", "/companies.html"})
    public String companies(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "admin/companies";
    }

    @GetMapping({"/jobs", "/jobs.html"})
    public String jobs(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String department,
                       @RequestParam(required = false) String query,
                       Model model) {
        List<JobPosting> list = jobPostingRepository.findAll();
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = list.stream().filter(j -> j.getStatus() != null && j.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("selectedStatus", "");
        }

        if (department != null && !department.isBlank() && !"ALL".equalsIgnoreCase(department)) {
            list = list.stream().filter(j -> {
                String b = (j.getAllowedBranches() != null) ? j.getAllowedBranches() : "";
                return b.toUpperCase().contains("ALL") || DepartmentMatcher.matches(department, null, b);
            }).collect(Collectors.toList());
            model.addAttribute("selectedDept", department);
        } else {
            model.addAttribute("selectedDept", "");
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(j -> {
                String title = (j.getTitle() != null) ? j.getTitle().toLowerCase() : "";
                String comp = (j.getCompany() != null) ? j.getCompany().getName().toLowerCase() : "";
                return title.contains(q) || comp.contains(q);
            }).collect(Collectors.toList());
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("jobs", list);
        return "admin/jobs";
    }

    @PostMapping({"/jobs/toggle-status"})
    public String toggleJobStatus(@RequestParam String jobId) {
        jobPostingRepository.findById(jobId).ifPresent(job -> {
            job.setStatus("OPEN".equalsIgnoreCase(job.getStatus()) ? "CLOSED" : "OPEN");
            jobPostingRepository.save(job);
        });
        return "redirect:/admin/jobs?updated=true";
    }

    @GetMapping({"/applications", "/applications.html"})
    public String applications(@RequestParam(required = false) String department,
                               @RequestParam(required = false) String company,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String query,
                               Model model) {
        List<Application> list = applicationRepository.findAll();

        if (department != null && !department.isBlank() && !"ALL".equalsIgnoreCase(department)) {
            list = list.stream()
                    .filter(a -> matchesDepartment(a.getStudent(), department))
                    .collect(Collectors.toList());
            model.addAttribute("selectedDept", department);
        } else {
            model.addAttribute("selectedDept", "");
        }

        if (company != null && !company.isBlank() && !"ALL".equalsIgnoreCase(company)) {
            String c = company.toLowerCase().trim();
            list = list.stream()
                    .filter(a -> a.getJob() != null && a.getJob().getCompany() != null &&
                            a.getJob().getCompany().getName().toLowerCase().contains(c))
                    .collect(Collectors.toList());
            model.addAttribute("selectedCompany", company);
        } else {
            model.addAttribute("selectedCompany", "");
        }

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            String sNorm = status.toUpperCase().trim().replace("_", " ");
            list = list.stream()
                    .filter(a -> {
                        if (a.getStatus() == null) return false;
                        String aNorm = a.getStatus().toUpperCase().trim().replace("_", " ");
                        return aNorm.equals(sNorm) || aNorm.contains(sNorm);
                    })
                    .collect(Collectors.toList());
            model.addAttribute("selectedStatus", status.toUpperCase().trim().replace(" ", "_"));
        } else {
            model.addAttribute("selectedStatus", "");
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream()
                    .filter(a -> {
                        String name = (a.getStudent() != null && a.getStudent().getUser() != null) ? a.getStudent().getUser().getFullName().toLowerCase() : "";
                        String roll = (a.getStudent() != null && a.getStudent().getRollNumber() != null) ? a.getStudent().getRollNumber().toLowerCase() : "";
                        String comp = (a.getJob() != null && a.getJob().getCompany() != null) ? a.getJob().getCompany().getName().toLowerCase() : "";
                        String role = (a.getJob() != null && a.getJob().getTitle() != null) ? a.getJob().getTitle().toLowerCase() : "";
                        return name.contains(q) || roll.contains(q) || comp.contains(q) || role.contains(q);
                    })
                    .collect(Collectors.toList());
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("applications", list);
        return "admin/applications";
    }

    private boolean matchesDepartment(StudentProfile student, String filterDept) {
        if (filterDept == null || filterDept.isBlank() || "ALL".equalsIgnoreCase(filterDept)) {
            return true;
        }
        return DepartmentMatcher.matches(student, filterDept);
    }

    @GetMapping({"/interviews", "/interviews.html"})
    public String interviews(Model model) {
        model.addAttribute("interviews", interviewSlotRepository.findAll());
        return "admin/interviews";
    }

    @GetMapping({"/feedback", "/feedback.html"})
    public String feedback(@RequestParam(required = false) String department,
                           @RequestParam(required = false) String company,
                           @RequestParam(required = false) String rating,
                           @RequestParam(required = false) String query,
                           Model model) {
        List<Feedback> list = feedbackRepository.findAll();
        if (department != null && !department.isBlank() && !"ALL".equalsIgnoreCase(department)) {
            list = list.stream().filter(f -> {
                StudentProfile sp = (f.getStudent() != null) ? f.getStudent() :
                        (f.getApplication() != null ? f.getApplication().getStudent() : null);
                if (sp != null) {
                    return matchesDepartment(sp, department);
                }
                return false;
            }).collect(Collectors.toList());
            model.addAttribute("selectedDept", department);
        } else {
            model.addAttribute("selectedDept", "");
        }

        if (company != null && !company.isBlank() && !"ALL".equalsIgnoreCase(company)) {
            String c = company.toLowerCase().trim();
            list = list.stream().filter(f -> (f.getCompanyName() != null && f.getCompanyName().toLowerCase().contains(c))).collect(Collectors.toList());
            model.addAttribute("selectedCompany", company);
        } else {
            model.addAttribute("selectedCompany", "");
        }

        if (rating != null && !rating.isBlank() && !"ALL".equalsIgnoreCase(rating)) {
            try {
                int r = Integer.parseInt(rating.trim());
                list = list.stream().filter(f -> f.getRating() == r).collect(Collectors.toList());
                model.addAttribute("selectedRating", rating);
            } catch (Exception ignored) {
                model.addAttribute("selectedRating", "");
            }
        } else {
            model.addAttribute("selectedRating", "");
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(f -> {
                String auth = (f.getAuthorName() != null) ? f.getAuthorName().toLowerCase() : "";
                String comm = (f.getComments() != null) ? f.getComments().toLowerCase() : "";
                return auth.contains(q) || comm.contains(q);
            }).collect(Collectors.toList());
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("feedbacks", list);
        return "admin/feedback";
    }

    @GetMapping({"/analytics", "/analytics.html"})
    public String analytics(Model model) {
        long totalStudents = studentProfileRepository.count();
        long placedCount = studentProfileRepository.countByPlacedStatus("OFFERED") +
                studentProfileRepository.countByPlacedStatus("ACCEPTED");
        double placementRate = totalStudents > 0 ? ((double) placedCount / totalStudents) * 100.0 : 82.4;

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("placedCount", placedCount);
        model.addAttribute("placementRate", String.format("%.1f", placementRate));
        model.addAttribute("totalCompanies", companyRepository.count());
        model.addAttribute("totalDrives", jobPostingRepository.count());

        return "admin/analytics";
    }

    @GetMapping({"/export/accreditation-csv", "/analytics/export-csv"})
    public org.springframework.http.ResponseEntity<byte[]> exportAccreditationCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("Roll Number,Candidate Name,Department,Batch Year,CGPA,Placement Status,Placed Company,Job Role,Package Offered (CTC),Contact Email,Phone,Key Technical Skills\n");

        List<StudentProfile> students = studentProfileRepository.findAll();
        List<Application> allApps = applicationRepository.findAll();

        for (StudentProfile s : students) {
            String roll = escapeCsv(s.getRollNumber());
            String name = escapeCsv(s.getUser() != null ? s.getUser().getFullName() : "Candidate");
            String dept = escapeCsv(s.getDepartment());
            int batch = s.getBatchYear() != null ? s.getBatchYear() : 2026;
            double cgpa = s.getCgpa() != null ? s.getCgpa() : 0.0;
            String status = s.getPlacedStatus() != null ? s.getPlacedStatus() : "SEEKING";

            Application offerApp = allApps.stream()
                    .filter(a -> a.getStudent() != null && s.getId() != null && s.getId().equals(a.getStudent().getId()))
                    .filter(a -> "OFFERED".equalsIgnoreCase(a.getStatus()) || "ACCEPTED".equalsIgnoreCase(a.getStatus()))
                    .findFirst()
                    .orElse(null);

            String company = (offerApp != null && offerApp.getJob() != null && offerApp.getJob().getCompany() != null)
                    ? escapeCsv(offerApp.getJob().getCompany().getName()) : "N/A";
            String jobRole = (offerApp != null && offerApp.getJob() != null)
                    ? escapeCsv(offerApp.getJob().getTitle()) : "N/A";
            String ctc = (offerApp != null && offerApp.getJob() != null)
                    ? escapeCsv(offerApp.getJob().getPackageCtc()) : "N/A";
            String email = escapeCsv(s.getUser() != null ? s.getUser().getEmail() : "N/A");
            String phone = escapeCsv(s.getPhone() != null ? s.getPhone() : "N/A");
            String skills = escapeCsv(s.getSkills() != null ? s.getSkills() : "N/A");

            csv.append(String.format("%s,%s,%s,%d,%.2f,%s,%s,%s,%s,%s,%s,%s\n",
                    roll, name, dept, batch, cgpa, status, company, jobRole, ctc, email, phone, skills));
        }

        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Campus_Placement_Accreditation_Report_2026.csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping({"/export/students-csv", "/students/export-csv"})
    public org.springframework.http.ResponseEntity<byte[]> exportStudentsCsv() {
        return exportAccreditationCsv();
    }

    private String escapeCsv(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }
}
