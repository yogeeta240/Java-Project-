package com.placement.controller;

import com.placement.model.*;
import com.placement.repository.*;
import com.placement.util.DepartmentMatcher;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final StudentProfileRepository studentProfileRepository;

    public RecruiterController(UserRepository userRepository,
                               RecruiterProfileRepository recruiterProfileRepository,
                               CompanyRepository companyRepository,
                               JobPostingRepository jobPostingRepository,
                               ApplicationRepository applicationRepository,
                               InterviewSlotRepository interviewSlotRepository,
                               StudentProfileRepository studentProfileRepository) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.applicationRepository = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    private RecruiterProfile getCurrentRecruiter(HttpSession session) {
        User loggedInUser = null;

        Object sessionUid = session.getAttribute("userId");
        if (sessionUid != null) {
            loggedInUser = userRepository.findById(String.valueOf(sessionUid)).orElse(null);
        }

        if (loggedInUser == null) {
            Object sessionEmail = session.getAttribute("userEmail");
            if (sessionEmail != null) {
                loggedInUser = userRepository.findByEmail(String.valueOf(sessionEmail).toLowerCase().trim()).orElse(null);
            }
        }

        if (loggedInUser == null) {
            Object sessionName = session.getAttribute("userName");
            if (sessionName != null) {
                String name = String.valueOf(sessionName).trim();
                loggedInUser = userRepository.findByRole(Role.RECRUITER).stream()
                        .filter(u -> u.getFullName() != null && u.getFullName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
            }
        }

        if (loggedInUser != null) {
            final User u = loggedInUser;
            // 1. Try repository direct find
            Optional<RecruiterProfile> profileOpt = recruiterProfileRepository.findByUser(u);
            if (profileOpt.isEmpty()) {
                // 2. Try in-memory search over existing profiles
                profileOpt = recruiterProfileRepository.findAll().stream()
                        .filter(p -> p.getUser() != null && (
                                (u.getId() != null && u.getId().equals(p.getUser().getId())) ||
                                (u.getEmail() != null && u.getEmail().equalsIgnoreCase(p.getUser().getEmail())) ||
                                (u.getFullName() != null && u.getFullName().equalsIgnoreCase(p.getUser().getFullName()))
                        ))
                        .findFirst();
            }

            boolean isDemoAccount = "recruiter@microsoft.com".equalsIgnoreCase(u.getEmail()) || "recruiter@google.com".equalsIgnoreCase(u.getEmail());

            if (profileOpt.isPresent()) {
                RecruiterProfile profile = profileOpt.get();
                // Self-healing: If not demo recruiter, but bound to Microsoft or Google, decouple to their own company
                if (!isDemoAccount && profile.getCompany() != null &&
                        ("Microsoft Corporation".equalsIgnoreCase(profile.getCompany().getName()) ||
                         "Microsoft".equalsIgnoreCase(profile.getCompany().getName()) ||
                         "Google".equalsIgnoreCase(profile.getCompany().getName()))) {
                    String orgName = (u.getFullName() != null && !u.getFullName().isBlank()) ? (u.getFullName().trim() + " Enterprise") : "Partner Organization";
                    Company dedicatedCompany = companyRepository.findByName(orgName).orElseGet(() ->
                            companyRepository.save(new Company(orgName, "Technology", "https://" + orgName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com", "Pan-India", "Campus hiring partner enterprise."))
                    );
                    profile.setCompany(dedicatedCompany);
                    profile = recruiterProfileRepository.save(profile);
                }
                return profile;
            }

            // 3. User exists but has no RecruiterProfile yet: create one with their own dedicated company
            String orgName = (u.getFullName() != null && !u.getFullName().isBlank()) ? (u.getFullName().trim() + " Enterprise") : "Partner Organization";
            Company defaultComp = companyRepository.findByName(orgName).orElseGet(() ->
                    companyRepository.save(new Company(orgName, "Technology", "https://" + orgName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com", "Pan-India", "Campus hiring partner enterprise."))
            );
            RecruiterProfile newProfile = new RecruiterProfile(u, defaultComp, "Corporate Talent Acquisition Partner", "+91 99887 00000");
            return recruiterProfileRepository.save(newProfile);
        }

        // Fallback to primary demo recruiter (Microsoft / Sarah)
        return recruiterProfileRepository.findAll().stream().findFirst().orElseGet(() -> {
            User user = userRepository.findByEmail("recruiter@microsoft.com").orElseGet(() ->
                    userRepository.save(new User("recruiter@microsoft.com", "password123", "Sarah Jenkins", Role.RECRUITER))
            );
            Company comp = companyRepository.findByName("Microsoft Corporation").orElseGet(() ->
                    companyRepository.save(new Company("Microsoft Corporation", "Software", "https://microsoft.com", "Bangalore", "Enterprise Cloud"))
            );
            return recruiterProfileRepository.save(new RecruiterProfile(user, comp, "Lead University Recruiter", "+91 99887 76655"));
        });
    }

    @GetMapping({"", "/", "/dashboard", "/dashboard.html"})
    public String dashboard(HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = recruiter != null ? recruiter.getCompany() : null;

        List<JobPosting> jobs = (company != null) ? jobPostingRepository.findByCompany(company) : Collections.emptyList();
        List<Application> allApplicants = jobs.stream()
                .flatMap(j -> applicationRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        long shortlistedCount = allApplicants.stream()
                .filter(a -> "SHORTLISTED".equalsIgnoreCase(a.getStatus()) || "TECHNICAL_ROUND".equalsIgnoreCase(a.getStatus()))
                .count();

        List<InterviewSlot> upcomingInterviews = jobs.stream()
                .flatMap(j -> interviewSlotRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", company);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("myJobsCount", jobs.size());
        model.addAttribute("totalApplicants", allApplicants.size());
        model.addAttribute("applicantCount", allApplicants.size());
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("upcomingInterviewsCount", upcomingInterviews.size());
        model.addAttribute("interviewsCount", upcomingInterviews.size());
        model.addAttribute("upcomingInterviews", upcomingInterviews);
        model.addAttribute("recentApplicants", allApplicants.stream().limit(5).collect(Collectors.toList()));

        return "recruiter/dashboard";
    }

    @GetMapping({"/profile", "/profile.html"})
    public String profile(HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = recruiter != null ? recruiter.getCompany() : null;

        List<JobPosting> jobs = (company != null) ? jobPostingRepository.findByCompany(company) : Collections.emptyList();
        List<Application> allApplicants = jobs.stream()
                .flatMap(j -> applicationRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        long shortlistedCount = allApplicants.stream()
                .filter(a -> "SHORTLISTED".equalsIgnoreCase(a.getStatus()) || "TECHNICAL_ROUND".equalsIgnoreCase(a.getStatus()))
                .count();

        List<InterviewSlot> upcomingInterviews = jobs.stream()
                .flatMap(j -> interviewSlotRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", company);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("myJobsCount", jobs.size());
        model.addAttribute("totalApplicants", allApplicants.size());
        model.addAttribute("applicantCount", allApplicants.size());
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("upcomingInterviewsCount", upcomingInterviews.size());
        model.addAttribute("interviewsCount", upcomingInterviews.size());
        model.addAttribute("upcomingInterviews", upcomingInterviews);
        return "recruiter/profile";
    }

    @PostMapping({"/profile", "/profile.html"})
    public String updateProfile(@RequestParam(required = false) String designation,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String companyName,
                                @RequestParam(required = false) String companyDescription,
                                @RequestParam(required = false) String website,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String industry,
                                HttpSession session) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        if (recruiter != null) {
            if (designation != null && !designation.isBlank()) recruiter.setDesignation(designation.trim());
            if (phone != null && !phone.isBlank()) recruiter.setPhone(phone.trim());
            recruiterProfileRepository.save(recruiter);

            Company company = recruiter.getCompany();
            if (company != null) {
                if (companyName != null && !companyName.isBlank()) company.setName(companyName.trim());
                if (companyDescription != null && !companyDescription.isBlank()) company.setDescription(companyDescription.trim());
                if (website != null && !website.isBlank()) company.setWebsite(website.trim());
                if (location != null && !location.isBlank()) company.setLocation(location.trim());
                if (industry != null && !industry.isBlank()) company.setIndustry(industry.trim());
                companyRepository.save(company);
            }
        }

        return "redirect:/recruiter/profile?updated=true";
    }

    @GetMapping({"/jobs", "/jobs.html"})
    public String jobs(HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = recruiter != null ? recruiter.getCompany() : null;
        List<JobPosting> jobs = (company != null) ? jobPostingRepository.findByCompany(company) : Collections.emptyList();

        Map<String, Long> applicantCountMap = new HashMap<>();
        for (JobPosting j : jobs) {
            applicantCountMap.put(j.getId(), (long) applicationRepository.findByJob(j).size());
        }

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", company);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        model.addAttribute("jobs", jobs);
        model.addAttribute("applicantCounts", applicantCountMap);
        return "recruiter/jobs";
    }

    @GetMapping({"/create-job", "/create-job.html"})
    public String createJobPage(HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", recruiter != null ? recruiter.getCompany() : null);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        return "recruiter/create-job";
    }

    @PostMapping({"/create-job", "/create-job.html", "/jobs/create"})
    public String handleCreateJob(@RequestParam(required = false) String title,
                                  @RequestParam(defaultValue = "Full-time Regular") String roleType,
                                  @RequestParam(required = false) String packageCtc,
                                  @RequestParam(required = false) String location,
                                  @RequestParam(defaultValue = "6.0") Double minCgpa,
                                  @RequestParam(defaultValue = "0") Integer maxBacklogs,
                                  @RequestParam(name = "allowedBranches", required = false) List<String> allowedBranches,
                                  @RequestParam(required = false) String skillsRequired,
                                  @RequestParam(required = false) String description,
                                  @RequestParam(required = false) String responsibilities,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                  HttpSession session) {
        if (title == null || title.trim().isBlank()) {
            return "redirect:/recruiter/create-job?error=missing_title";
        }
        if (packageCtc == null || packageCtc.trim().isBlank()) {
            return "redirect:/recruiter/create-job?error=missing_package";
        }

        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = (recruiter != null) ? recruiter.getCompany() : null;
        if (company == null) {
            company = companyRepository.findAll().stream().findFirst().orElseGet(() ->
                    companyRepository.save(new Company("Partner Enterprise", "Technology", "https://enterprise.example.com", "Pan-India", "Campus partner"))
            );
        }

        Double numericCtc = 10.0;
        try {
            String numOnly = packageCtc.replaceAll("[^0-9.]", "");
            if (!numOnly.isBlank()) numericCtc = Double.parseDouble(numOnly);
        } catch (Exception ignored) {}

        LocalDate effectiveDeadline = (deadline != null) ? deadline : LocalDate.now().plusDays(30);

        String effectiveBranches = (allowedBranches != null && !allowedBranches.isEmpty())
                ? String.join(", ", allowedBranches)
                : "CSE, IT, ECE";

        String fullDescription = (description != null && !description.isBlank()) ? description.trim() : "Campus placement opportunity.";
        if (responsibilities != null && !responsibilities.isBlank()) {
            fullDescription += "\n\nKey Responsibilities:\n" + responsibilities.trim();
        }

        JobPosting job = new JobPosting(
                company,
                title.trim(),
                (roleType != null && !roleType.isBlank()) ? roleType.trim() : "Full-time Regular",
                packageCtc.trim(),
                numericCtc,
                (location != null && !location.isBlank()) ? location.trim() : "Pan-India",
                (minCgpa != null) ? minCgpa : 6.0,
                (maxBacklogs != null) ? maxBacklogs : 0,
                effectiveBranches,
                (skillsRequired != null && !skillsRequired.isBlank()) ? skillsRequired.trim() : "Core Engineering Fundamentals",
                fullDescription,
                effectiveDeadline
        );
        jobPostingRepository.save(job);

        return "redirect:/recruiter/jobs?created=true";
    }

    @GetMapping({"/applicants", "/applicants.html"})
    public String applicants(@RequestParam(required = false) String jobId,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String query,
                             HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        List<JobPosting> jobs = jobPostingRepository.findByCompany(recruiter.getCompany());

        List<Application> applicants;
        if (jobId != null && !jobId.isBlank()) {
            JobPosting target = jobPostingRepository.findById(jobId).orElse(null);
            applicants = (target != null) ? applicationRepository.findByJob(target) : Collections.emptyList();
            model.addAttribute("selectedJobId", jobId);
        } else {
            applicants = jobs.stream()
                    .flatMap(j -> applicationRepository.findByJob(j).stream())
                    .collect(Collectors.toList());
            model.addAttribute("selectedJobId", null);
        }

        if (department != null && !department.isBlank() && !"ALL".equalsIgnoreCase(department)) {
            applicants = applicants.stream()
                    .filter(a -> matchesDepartment(a.getStudent(), department))
                    .collect(Collectors.toList());
            model.addAttribute("selectedDept", department);
        } else {
            model.addAttribute("selectedDept", "");
        }

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            String statusNorm = status.toUpperCase().trim().replace("_", " ");
            applicants = applicants.stream()
                    .filter(a -> {
                        if (a.getStatus() == null) return false;
                        String appNorm = a.getStatus().toUpperCase().trim().replace("_", " ");
                        return appNorm.equals(statusNorm) || appNorm.contains(statusNorm);
                    })
                    .collect(Collectors.toList());
            model.addAttribute("selectedStatus", status.toUpperCase().trim().replace(" ", "_"));
        } else {
            model.addAttribute("selectedStatus", "");
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            applicants = applicants.stream()
                    .filter(a -> {
                        String name = (a.getStudent() != null && a.getStudent().getUser() != null) ? a.getStudent().getUser().getFullName().toLowerCase() : "";
                        String roll = (a.getStudent() != null && a.getStudent().getRollNumber() != null) ? a.getStudent().getRollNumber().toLowerCase() : "";
                        String skills = (a.getStudent() != null && a.getStudent().getSkills() != null) ? a.getStudent().getSkills().toLowerCase() : "";
                        String dept = (a.getStudent() != null && a.getStudent().getDepartment() != null) ? a.getStudent().getDepartment().toLowerCase() : "";
                        return name.contains(q) || roll.contains(q) || skills.contains(q) || dept.contains(q);
                    })
                    .collect(Collectors.toList());
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", recruiter != null ? recruiter.getCompany() : null);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        model.addAttribute("jobs", jobs);
        model.addAttribute("applicants", applicants);
        return "recruiter/applicants";
    }

    private boolean matchesDepartment(StudentProfile student, String filterDept) {
        if (filterDept == null || filterDept.isBlank() || "ALL".equalsIgnoreCase(filterDept)) {
            return true;
        }
        return DepartmentMatcher.matches(student, filterDept);
    }

    @PostMapping({"/update-applicant-status", "/applicants/update-status"})
    public String updateApplicantStatus(@RequestParam String applicationId,
                                        @RequestParam String status,
                                        @RequestParam(required = false) String notes) {
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            app.setStatus(status.toUpperCase());
            if (notes != null && !notes.isBlank()) {
                app.setFeedbackNotes(notes);
            }
            applicationRepository.save(app);

            // If offer extended, update student placedStatus
            if ("OFFERED".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status)) {
                StudentProfile sp = app.getStudent();
                if (sp != null) {
                    sp.setPlacedStatus("OFFERED");
                    studentProfileRepository.save(sp);
                }
            }
        }
        return "redirect:/recruiter/applicants?updated=true";
    }

    @GetMapping({"/interviews", "/interviews.html"})
    public String interviews(HttpSession session, Model model) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = recruiter != null ? recruiter.getCompany() : null;
        List<JobPosting> jobs = (company != null) ? jobPostingRepository.findByCompany(company) : Collections.emptyList();
        List<InterviewSlot> interviews = jobs.stream()
                .flatMap(j -> interviewSlotRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        List<StudentProfile> candidates = jobs.stream()
                .flatMap(j -> applicationRepository.findByJob(j).stream())
                .map(Application::getStudent)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("company", company);
        model.addAttribute("recruiterName", (recruiter != null && recruiter.getUser() != null) ? recruiter.getUser().getFullName() : "Recruiter");
        model.addAttribute("jobs", jobs);
        model.addAttribute("interviews", interviews);
        model.addAttribute("candidates", candidates);
        return "recruiter/interviews";
    }

    @PostMapping({"/schedule-interview", "/interviews/schedule"})
    public String scheduleInterview(@RequestParam String jobId,
                                    @RequestParam String studentId,
                                    @RequestParam String roundName,
                                    @RequestParam(required = false) String interviewDate,
                                    @RequestParam(required = false) String interviewTime,
                                    @RequestParam(defaultValue = "45") Integer durationMinutes,
                                    @RequestParam(required = false) String meetingLinkOrVenue,
                                    @RequestParam(required = false) String notes) {
        Optional<JobPosting> jobOpt = jobPostingRepository.findById(jobId);
        Optional<StudentProfile> studentOpt = studentProfileRepository.findById(studentId);

        if (jobOpt.isPresent() && studentOpt.isPresent()) {
            LocalDateTime scheduled = LocalDateTime.now().plusDays(3).withHour(10).withMinute(30);
            try {
                if (interviewDate != null && !interviewDate.isBlank()) {
                    LocalDate d = LocalDate.parse(interviewDate);
                    if (interviewTime != null && !interviewTime.isBlank()) {
                        java.time.LocalTime t = java.time.LocalTime.parse(interviewTime);
                        scheduled = LocalDateTime.of(d, t);
                    } else {
                        scheduled = d.atTime(10, 0);
                    }
                }
            } catch (Exception ignored) {}

            InterviewSlot slot = new InterviewSlot(
                    jobOpt.get(), studentOpt.get(), roundName,
                    scheduled,
                    durationMinutes,
                    meetingLinkOrVenue != null && !meetingLinkOrVenue.isBlank() ? meetingLinkOrVenue : "Virtual Conference Room",
                    notes != null ? notes : "Technical assessment round."
            );
            interviewSlotRepository.save(slot);

            // Update application status to TECHNICAL_ROUND if applicable
            applicationRepository.findByJobAndStudent(jobOpt.get(), studentOpt.get()).ifPresent(app -> {
                app.setStatus("TECHNICAL_ROUND");
                applicationRepository.save(app);
            });
        }

        return "redirect:/recruiter/interviews?scheduled=true";
    }

    @GetMapping({"/export/applicants-csv", "/applicants/export-csv"})
    public org.springframework.http.ResponseEntity<byte[]> exportApplicantsCsv(HttpSession session) {
        RecruiterProfile recruiter = getCurrentRecruiter(session);
        Company company = recruiter != null ? recruiter.getCompany() : null;
        List<JobPosting> jobs = (company != null) ? jobPostingRepository.findByCompany(company) : Collections.emptyList();
        List<Application> applicants = jobs.stream()
                .flatMap(j -> applicationRepository.findByJob(j).stream())
                .collect(Collectors.toList());

        StringBuilder csv = new StringBuilder();
        csv.append("Candidate Name,University Roll Number,Department,CGPA,Job Title,Package CTC,AI Match Score,Applied Date,Application Status,Feedback Notes,Email,Phone,Skills\n");

        for (Application app : applicants) {
            StudentProfile s = app.getStudent();
            String name = (s != null && s.getUser() != null) ? escapeCsv(s.getUser().getFullName()) : "Candidate";
            String roll = s != null ? escapeCsv(s.getRollNumber()) : "N/A";
            String dept = s != null ? escapeCsv(s.getDepartment()) : "N/A";
            double cgpa = s != null && s.getCgpa() != null ? s.getCgpa() : 0.0;
            String jobTitle = app.getJob() != null ? escapeCsv(app.getJob().getTitle()) : "Job Position";
            String ctc = app.getJob() != null ? escapeCsv(app.getJob().getPackageCtc()) : "N/A";
            int matchScore = app.getAiMatchScore() != null ? app.getAiMatchScore() : 0;
            String appliedDate = app.getAppliedAt() != null ? escapeCsv(app.getAppliedAt().toString()) : "N/A";
            String status = escapeCsv(app.getStatus());
            String notes = escapeCsv(app.getFeedbackNotes());
            String email = (s != null && s.getUser() != null) ? escapeCsv(s.getUser().getEmail()) : "N/A";
            String phone = (s != null && s.getPhone() != null) ? escapeCsv(s.getPhone()) : "N/A";
            String skills = (s != null && s.getSkills() != null) ? escapeCsv(s.getSkills()) : "N/A";

            csv.append(String.format("%s,%s,%s,%.2f,%s,%s,%d%%,%s,%s,%s,%s,%s,%s\n",
                    name, roll, dept, cgpa, jobTitle, ctc, matchScore, appliedDate, status, notes, email, phone, skills));
        }

        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Candidate_Screening_Report_2026.csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    private String escapeCsv(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }
}
