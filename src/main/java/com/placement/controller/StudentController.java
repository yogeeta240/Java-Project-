package com.placement.controller;

import com.placement.model.*;
import com.placement.repository.*;
import com.placement.service.AiRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final FeedbackRepository feedbackRepository;
    private final AiRecommendationService aiRecommendationService;
    private final com.placement.service.ResumePdfService resumePdfService;

    public StudentController(UserRepository userRepository,
                             StudentProfileRepository studentProfileRepository,
                             JobPostingRepository jobPostingRepository,
                             ApplicationRepository applicationRepository,
                             InterviewSlotRepository interviewSlotRepository,
                             FeedbackRepository feedbackRepository,
                             AiRecommendationService aiRecommendationService,
                             com.placement.service.ResumePdfService resumePdfService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.applicationRepository = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.feedbackRepository = feedbackRepository;
        this.aiRecommendationService = aiRecommendationService;
        this.resumePdfService = resumePdfService;
    }

    private StudentProfile getCurrentStudent(HttpSession session) {
        Object sessionUid = session.getAttribute("userId");
        if (sessionUid != null) {
            String userId = String.valueOf(sessionUid);
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<StudentProfile> profileOpt = studentProfileRepository.findByUser(user);
                if (profileOpt.isPresent()) return profileOpt.get();

                // If profile doesn't exist yet for this logged-in student user, initialize one for them
                StudentProfile newProfile = new StudentProfile();
                newProfile.setUser(user);
                String roll = "2026-" + (user.getId().length() > 6 ? user.getId().substring(user.getId().length() - 6) : user.getId());
                newProfile.setRollNumber(roll);
                newProfile.setDepartment("Computer Science & Engineering");
                newProfile.setBatchYear(2026);
                newProfile.setCgpa(8.0);
                newProfile.setActiveBacklogs(0);
                newProfile.setSkills("Java, Python, SQL");
                return studentProfileRepository.save(newProfile);
            }
        }
        // Fallback to primary demo student (Aarav) only when no session user exists
        return studentProfileRepository.findAll().stream().findFirst().orElseGet(() -> {
            User user = userRepository.findByEmail("aarav@univ.edu").orElseGet(() ->
                    userRepository.save(new User("aarav@univ.edu", "password123", "Aarav Sharma", Role.STUDENT))
            );
            return studentProfileRepository.save(new StudentProfile(user, "2022CSE041", "Computer Science & Engineering", 2026, 8.92, 94.5, 92.0, 0, "Java, Spring Boot, React, SQL", "+91 98765 43210"));
        });
    }

    @GetMapping({"", "/", "/dashboard", "/dashboard.html"})
    public String dashboard(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        model.addAttribute("student", student);
        model.addAttribute("studentName", student.getUser() != null ? student.getUser().getFullName() : "Student");
        model.addAttribute("rollNumber", student.getRollNumber() != null ? student.getRollNumber() : "");

        List<Application> applications = applicationRepository.findByStudent(student);
        model.addAttribute("applications", applications);
        model.addAttribute("totalApplied", applications.size());
        model.addAttribute("applicationsCount", applications.size());

        long shortlistedCount = applications.stream()
                .filter(a -> "SHORTLISTED".equalsIgnoreCase(a.getStatus()) || "TECHNICAL_ROUND".equalsIgnoreCase(a.getStatus()))
                .count();
        model.addAttribute("shortlistedCount", shortlistedCount);

        long offersCount = applications.stream()
                .filter(a -> "OFFERED".equalsIgnoreCase(a.getStatus()) || "ACCEPTED".equalsIgnoreCase(a.getStatus()))
                .count();
        model.addAttribute("offersCount", offersCount);

        List<InterviewSlot> interviews = interviewSlotRepository.findByStudent(student);
        model.addAttribute("interviews", interviews);
        model.addAttribute("upcomingInterviewsCount", interviews.size());
        model.addAttribute("interviewsCount", interviews.size());

        long activeDrivesCount = jobPostingRepository.countByStatus("OPEN");
        model.addAttribute("activeDrivesCount", activeDrivesCount);

        // Dynamic AI recommended jobs for dashboard widget
        List<JobPosting> allJobs = jobPostingRepository.findByStatus("OPEN");
        Map<JobPosting, Integer> jobScores = new LinkedHashMap<>();
        for (JobPosting job : allJobs) {
            jobScores.put(job, aiRecommendationService.calculateMatchScore(student, job));
        }

        List<Map.Entry<JobPosting, Integer>> topRecommendations = jobScores.entrySet().stream()
                .sorted(Map.Entry.<JobPosting, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
        model.addAttribute("recommendedEntries", topRecommendations);

        if (!topRecommendations.isEmpty()) {
            model.addAttribute("topJob", topRecommendations.get(0).getKey());
            model.addAttribute("topScore", topRecommendations.get(0).getValue());
        }

        return "student/dashboard";
    }

    @GetMapping({"/profile", "/profile.html"})
    public String profile(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        model.addAttribute("student", student);
        model.addAttribute("studentName", student.getUser() != null ? student.getUser().getFullName() : "Student");
        model.addAttribute("rollNumber", student.getRollNumber() != null ? student.getRollNumber() : "");
        return "student/profile";
    }

    @GetMapping({"/resume/download", "/resume/download.html"})
    public org.springframework.http.ResponseEntity<byte[]> downloadResume(HttpSession session) {
        StudentProfile student = getCurrentStudent(session);

        // 1. If student uploaded a custom resume document, serve that exact file
        if (student.getResumeData() != null && student.getResumeData().length > 0) {
            String candidateName = (student.getUser() != null && student.getUser().getFullName() != null)
                    ? student.getUser().getFullName().trim().replaceAll("\\s+", "_")
                    : "Candidate";
            String filename = (student.getResumeFileName() != null && !student.getResumeFileName().isBlank())
                    ? student.getResumeFileName()
                    : candidateName + "_Resume.pdf";
            String contentType = (student.getResumeContentType() != null && !student.getResumeContentType().isBlank())
                    ? student.getResumeContentType()
                    : "application/pdf";

            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(student.getResumeData());
        }

        // 2. Default fallback: Generate institutional placement dossier PDF
        byte[] pdfBytes = resumePdfService.generateStudentResume(student);
        String candidateName = (student.getUser() != null && student.getUser().getFullName() != null)
                ? student.getUser().getFullName().trim().replaceAll("\\s+", "_")
                : "Candidate";
        String filename = candidateName + "_Resume.pdf";

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping({"/resume/upload", "/resume/upload.html"})
    public String uploadResume(@RequestParam("resumeFile") org.springframework.web.multipart.MultipartFile file,
                               HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (file != null && !file.isEmpty()) {
            try {
                student.setResumeData(file.getBytes());
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && !originalFilename.isBlank()) {
                    student.setResumeFileName(new java.io.File(originalFilename).getName());
                } else {
                    String candidateName = (student.getUser() != null && student.getUser().getFullName() != null)
                            ? student.getUser().getFullName().trim().replaceAll("\\s+", "_")
                            : "Candidate";
                    student.setResumeFileName(candidateName + "_Resume.pdf");
                }
                String contentType = file.getContentType();
                student.setResumeContentType(contentType != null ? contentType : "application/pdf");
                student.setResumeUrl("/student/resume/download");
                studentProfileRepository.save(student);

                // Also save backup to disk
                try {
                    java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads", "resumes");
                    if (!java.nio.file.Files.exists(uploadDir)) {
                        java.nio.file.Files.createDirectories(uploadDir);
                    }
                    java.nio.file.Path targetPath = uploadDir.resolve(student.getId() + "_" + student.getResumeFileName());
                    java.nio.file.Files.write(targetPath, file.getBytes());
                } catch (Exception ignored) {}

                return "redirect:/student/profile?resumeUploaded=true";
            } catch (Exception e) {
                return "redirect:/student/profile?error=upload_failed";
            }
        }
        return "redirect:/student/profile";
    }

    @PostMapping({"/certifications/add", "/certifications/add.html"})
    public String addCertification(@RequestParam String title,
                                   @RequestParam(required = false) String issuingOrganization,
                                   @RequestParam(required = false) String issueDate,
                                   @RequestParam(required = false) String credentialId,
                                   @RequestParam(required = false) org.springframework.web.multipart.MultipartFile certificateFile,
                                   HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (title != null && !title.isBlank()) {
            com.placement.model.Certification cert = new com.placement.model.Certification(
                    title.trim(),
                    (issuingOrganization != null && !issuingOrganization.isBlank()) ? issuingOrganization.trim() : "Accreditation Authority",
                    (issueDate != null && !issueDate.isBlank()) ? issueDate.trim() : "2026",
                    (credentialId != null && !credentialId.isBlank()) ? credentialId.trim() : "CERT-" + System.currentTimeMillis() % 100000
            );

            if (certificateFile != null && !certificateFile.isEmpty()) {
                try {
                    cert.setCertificateData(certificateFile.getBytes());
                    String origName = certificateFile.getOriginalFilename();
                    cert.setCertificateFileName(origName != null ? new java.io.File(origName).getName() : title.trim().replaceAll("\\s+", "_") + "_Certificate.pdf");
                    cert.setCertificateContentType(certificateFile.getContentType() != null ? certificateFile.getContentType() : "application/pdf");
                } catch (Exception ignored) {}
            }

            student.getCertifications().add(cert);
            studentProfileRepository.save(student);
            return "redirect:/student/profile?certAdded=true";
        }
        return "redirect:/student/profile";
    }

    @GetMapping({"/certifications/download", "/certifications/download.html"})
    public org.springframework.http.ResponseEntity<byte[]> downloadCertificate(@RequestParam(required = false) String id,
                                                                               @RequestParam(required = false, defaultValue = "0") int index,
                                                                               HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        com.placement.model.Certification targetCert = null;

        if (id != null && !id.isBlank()) {
            for (com.placement.model.Certification c : student.getCertifications()) {
                if (id.equals(c.getId())) {
                    targetCert = c;
                    break;
                }
            }
        }

        if (targetCert == null && student.getCertifications() != null && !student.getCertifications().isEmpty()) {
            int idx = Math.max(0, Math.min(index, student.getCertifications().size() - 1));
            targetCert = student.getCertifications().get(idx);
        }

        if (targetCert == null) {
            targetCert = new com.placement.model.Certification("Professional Technical Accreditation", "University Placement Board", "2026", "CERT-88492");
        }

        // If candidate uploaded custom certificate file, stream that
        if (targetCert.getCertificateData() != null && targetCert.getCertificateData().length > 0) {
            String filename = (targetCert.getCertificateFileName() != null && !targetCert.getCertificateFileName().isBlank())
                    ? targetCert.getCertificateFileName()
                    : targetCert.getTitle().replaceAll("\\s+", "_") + "_Certificate.pdf";
            String contentType = (targetCert.getCertificateContentType() != null && !targetCert.getCertificateContentType().isBlank())
                    ? targetCert.getCertificateContentType()
                    : "application/pdf";

            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(targetCert.getCertificateData());
        }

        // Otherwise generate high-resolution certificate PDF
        byte[] certPdf = resumePdfService.generateCertificatePdf(student, targetCert);
        String filename = targetCert.getTitle().replaceAll("\\s+", "_") + "_Certificate.pdf";

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(certPdf);
    }

    @PostMapping({"/certifications/delete"})
    public String deleteCertification(@RequestParam String certId, HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (certId != null && student.getCertifications() != null) {
            student.getCertifications().removeIf(c -> certId.equals(c.getId()));
            studentProfileRepository.save(student);
        }
        return "redirect:/student/profile?certDeleted=true";
    }

    @PostMapping({"/profile", "/profile.html"})
    public String updateProfile(@RequestParam(required = false) String skills,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) Double cgpa,
                                HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (skills != null) student.setSkills(skills);
        if (phone != null) student.setPhone(phone);
        if (cgpa != null) student.setCgpa(cgpa);
        studentProfileRepository.save(student);
        return "redirect:/student/profile?updated=true";
    }

    @PostMapping({"/projects/add", "/projects/add.html"})
    public String addProject(@RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String technologies,
                             @RequestParam(required = false) String repoUrl,
                             HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (title != null && !title.isBlank()) {
            student.getProjects().add(new AcademicProject(
                    title.trim(),
                    description != null && !description.isBlank() ? description.trim() : "Academic portfolio project demonstrating advanced computer science and software development skills.",
                    technologies != null && !technologies.isBlank() ? technologies.trim() : "General Engineering",
                    repoUrl != null && !repoUrl.isBlank() ? repoUrl.trim() : "#"
            ));
            studentProfileRepository.save(student);
        }
        return "redirect:/student/profile?projectAdded=true";
    }

    @PostMapping({"/projects/delete", "/projects/delete.html"})
    public String deleteProject(@RequestParam int projectIndex, HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        if (student.getProjects() != null && projectIndex >= 0 && projectIndex < student.getProjects().size()) {
            student.getProjects().remove(projectIndex);
            studentProfileRepository.save(student);
        }
        return "redirect:/student/profile?projectDeleted=true";
    }

    @GetMapping({"/jobs", "/jobs.html"})
    public String jobs(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String branch,
                       HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        model.addAttribute("student", student);

        List<JobPosting> jobs = jobPostingRepository.findByStatus("OPEN");
        if (search != null && !search.isBlank()) {
            String query = search.toLowerCase();
            jobs = jobs.stream()
                    .filter(j -> (j.getTitle() != null && j.getTitle().toLowerCase().contains(query)) ||
                            (j.getCompany() != null && j.getCompany().getName() != null && j.getCompany().getName().toLowerCase().contains(query)) ||
                            (j.getSkillsRequired() != null && j.getSkillsRequired().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        // Compute AI match score and eligibility for each job
        Map<String, Integer> matchScores = new HashMap<>();
        Map<String, Boolean> eligibilityMap = new HashMap<>();
        Set<String> appliedJobIds = applicationRepository.findByStudent(student).stream()
                .filter(a -> a.getJob() != null && a.getJob().getId() != null)
                .map(a -> a.getJob().getId())
                .collect(Collectors.toSet());

        for (JobPosting j : jobs) {
            matchScores.put(j.getId(), aiRecommendationService.calculateMatchScore(student, j));
            eligibilityMap.put(j.getId(), aiRecommendationService.isEligible(student, j));
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("matchScores", matchScores);
        model.addAttribute("eligibilityMap", eligibilityMap);
        model.addAttribute("appliedJobIds", appliedJobIds);
        return "student/jobs";
    }

    @GetMapping({"/job-details", "/job-details.html"})
    public String jobDetails(@RequestParam(required = false) String id,
                             HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        JobPosting job = null;
        if (id != null && !id.isBlank()) {
            job = jobPostingRepository.findById(id).orElse(null);
        }
        if (job == null) {
            job = jobPostingRepository.findAll().stream().findFirst().orElse(null);
        }

        model.addAttribute("student", student);
        model.addAttribute("job", job);

        if (job != null) {
            int matchScore = aiRecommendationService.calculateMatchScore(student, job);
            boolean isEligible = aiRecommendationService.isEligible(student, job);
            boolean alreadyApplied = applicationRepository.findByJobAndStudent(job, student).isPresent();

            model.addAttribute("matchScore", matchScore);
            model.addAttribute("isEligible", isEligible);
            model.addAttribute("alreadyApplied", alreadyApplied);
        }

        return "student/job-details";
    }

    @PostMapping("/apply")
    public String applyForJob(@RequestParam String jobId, HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        Optional<JobPosting> jobOpt = jobPostingRepository.findById(jobId);

        if (jobOpt.isPresent()) {
            JobPosting job = jobOpt.get();
            if (applicationRepository.findByJobAndStudent(job, student).isEmpty()) {
                int score = aiRecommendationService.calculateMatchScore(student, job);
                Application application = new Application(job, student, score);
                applicationRepository.save(application);
            }
        }
        return "redirect:/student/applications?applied=true";
    }

    @GetMapping({"/applications", "/applications.html"})
    public String applications(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        List<Application> applications = applicationRepository.findByStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("applications", applications);
        return "student/applications";
    }

    @GetMapping({"/interviews", "/interviews.html"})
    public String interviews(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        List<InterviewSlot> interviews = interviewSlotRepository.findByStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("interviews", interviews);
        return "student/interviews";
    }

    @GetMapping({"/recommendations", "/recommendations.html"})
    public String recommendations(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        model.addAttribute("student", student);

        List<JobPosting> jobs = jobPostingRepository.findByStatus("OPEN");
        List<Map<String, Object>> recommendationsList = new ArrayList<>();

        for (JobPosting job : jobs) {
            Map<String, Object> entry = new HashMap<>();
            int score = aiRecommendationService.calculateMatchScore(student, job);
            boolean eligible = aiRecommendationService.isEligible(student, job);

            entry.put("job", job);
            entry.put("score", score);
            entry.put("eligible", eligible);
            recommendationsList.add(entry);
        }

        recommendationsList.sort((a, b) -> Integer.compare((Integer) b.get("score"), (Integer) a.get("score")));
        model.addAttribute("recommendations", recommendationsList);

        return "student/recommendations";
    }

    @GetMapping({"/feedback", "/feedback.html"})
    public String feedback(HttpSession session, Model model) {
        StudentProfile student = getCurrentStudent(session);
        model.addAttribute("student", student);

        // Ensure privacy: Students can only view their own feedback submissions
        List<Feedback> studentFeedbacks = feedbackRepository.findAll().stream()
                .filter(f -> isFeedbackByStudent(f, student))
                .sorted((a, b) -> {
                    LocalDateTime t1 = a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN;
                    LocalDateTime t2 = b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.MIN;
                    return t2.compareTo(t1);
                })
                .collect(Collectors.toList());

        model.addAttribute("feedbacks", studentFeedbacks);
        model.addAttribute("applications", applicationRepository.findByStudent(student));
        return "student/feedback";
    }

    private boolean isFeedbackByStudent(Feedback f, StudentProfile student) {
        if (f == null || student == null) return false;

        // 1. Direct student reference
        if (f.getStudent() != null && f.getStudent().getId() != null && student.getId() != null) {
            return f.getStudent().getId().equals(student.getId());
        }

        // 2. Reference through student's application
        if (f.getApplication() != null && f.getApplication().getStudent() != null &&
                f.getApplication().getStudent().getId() != null && student.getId() != null) {
            return f.getApplication().getStudent().getId().equals(student.getId());
        }

        // 3. Fallback for legacy seeded entries: match author name only if role is STUDENT
        if ("STUDENT".equalsIgnoreCase(f.getSubmittedByRole()) && f.getAuthorName() != null &&
                student.getUser() != null && student.getUser().getFullName() != null) {
            return f.getAuthorName().trim().equalsIgnoreCase(student.getUser().getFullName().trim());
        }

        return false;
    }

    @PostMapping({"/feedback", "/feedback.html"})
    public String submitFeedback(@RequestParam(required = false) String applicationId,
                                 @RequestParam(required = false) String companyOrDrive,
                                 @RequestParam(required = false, defaultValue = "5") Integer rating,
                                 @RequestParam String feedbackText,
                                 HttpSession session) {
        StudentProfile student = getCurrentStudent(session);
        Application application = null;
        if (applicationId != null && !applicationId.isBlank()) {
            Application found = applicationRepository.findById(applicationId).orElse(null);
            // Verify application belongs to the logged-in student
            if (found != null && found.getStudent() != null && student.getId() != null &&
                    student.getId().equals(found.getStudent().getId())) {
                application = found;
            }
        }

        String comp = companyOrDrive;
        if ((comp == null || comp.isBlank()) && application != null && application.getJob() != null) {
            comp = application.getJob().getCompany().getName();
        }

        String authorName = (student.getUser() != null && student.getUser().getFullName() != null)
                ? student.getUser().getFullName() : "Student";

        Feedback feedback = new Feedback(application, student, "STUDENT", authorName,
                (comp != null && !comp.isBlank()) ? comp : "Campus Placement Drive", rating, feedbackText);
        feedbackRepository.save(feedback);

        return "redirect:/student/feedback?submitted=true";
    }
}
