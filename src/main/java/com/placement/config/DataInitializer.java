package com.placement.config;

import com.placement.model.*;
import com.placement.repository.*;
import com.placement.service.AiRecommendationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final FeedbackRepository feedbackRepository;
    private final AiRecommendationService aiRecommendationService;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           CompanyRepository companyRepository,
                           RecruiterProfileRepository recruiterProfileRepository,
                           JobPostingRepository jobPostingRepository,
                           ApplicationRepository applicationRepository,
                           InterviewSlotRepository interviewSlotRepository,
                           FeedbackRepository feedbackRepository,
                           AiRecommendationService aiRecommendationService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.applicationRepository = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.feedbackRepository = feedbackRepository;
        this.aiRecommendationService = aiRecommendationService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            ensureDiverseCandidatesForDemo();
            return;
        }

        System.out.println("[DataInitializer] Seeding institutional demo data...");

        // 1. Seed Users
        User studentUser1 = userRepository.save(new User("aarav@univ.edu", "password123", "Aarav Sharma", Role.STUDENT));
        User studentUser2 = userRepository.save(new User("priya@univ.edu", "password123", "Priya Patel", Role.STUDENT));
        User studentUser3 = userRepository.save(new User("rohit@univ.edu", "password123", "Rohit Kumar", Role.STUDENT));

        User recruiterUser1 = userRepository.save(new User("recruiter@microsoft.com", "password123", "Sarah Jenkins", Role.RECRUITER));
        User recruiterUser2 = userRepository.save(new User("recruiter@google.com", "password123", "Amit Verma", Role.RECRUITER));

        User adminUser = userRepository.save(new User("dean@univ.edu", "password123", "Dr. Rajesh Varma", Role.ADMIN));

        // 2. Seed Student Profiles
        StudentProfile aarav = studentProfileRepository.save(new StudentProfile(
                studentUser1, "2022CSE041", "Computer Science & Engineering", 2026, 8.92,
                94.5, 92.0, 0,
                "Java, Spring Boot, Python, React, SQL, Docker, Microservices, Machine Learning",
                "+91 98765 43210"
        ));
        aarav.setResumeUrl("/uploads/resumes/Aarav_Sharma_Resume_2026.pdf");
        aarav.getProjects().add(new AcademicProject(
                "AI-Powered Placement Management System",
                "Full-stack university portal connecting students, recruiters, and administrators with intelligent skill-match algorithms.",
                "Java, Spring Boot, Thymeleaf, MongoDB",
                "https://github.com/placement-ai/portal"
        ));
        aarav.getProjects().add(new AcademicProject(
                "Distributed Microservices Banking Prototype",
                "Event-driven banking transaction core implementing idempotent payment processing and transaction ledgering.",
                "Java, Spring Cloud, PostgreSQL, Docker",
                "https://github.com/placement-ai/banking-core"
        ));
        aarav.getCertifications().add(new Certification(
                "Oracle Certified Professional: Java SE 17 Developer",
                "Oracle University",
                "Aug 2025",
                "OCP-JAVA-884920"
        ));
        aarav.getCertifications().add(new Certification(
                "AWS Certified Solutions Architect - Associate",
                "Amazon Web Services",
                "Nov 2025",
                "AWS-SAA-349021"
        ));
        studentProfileRepository.save(aarav);

        StudentProfile priya = studentProfileRepository.save(new StudentProfile(
                studentUser2, "2022IT018", "Information Technology", 2026, 9.15,
                96.0, 95.0, 0,
                "Python, TensorFlow, Deep Learning, SQL, PyTorch, Data Structures",
                "+91 98765 43211"
        ));
        priya.setPlacedStatus("OFFERED");
        studentProfileRepository.save(priya);

        StudentProfile rohit = studentProfileRepository.save(new StudentProfile(
                studentUser3, "2022ECE055", "Electronics & Communication", 2026, 7.85,
                88.0, 85.0, 1,
                "C++, Embedded Systems, IoT, Python, Verilog",
                "+91 98765 43212"
        ));

        // 3. Seed Companies
        Company msft = companyRepository.save(new Company(
                "Microsoft Corporation", "Software & Cloud", "https://careers.microsoft.com",
                "Redmond, WA / Hyderabad, India",
                "Global technology leader empowering every person and organization on the planet to achieve more."
        ));

        Company google = companyRepository.save(new Company(
                "Google", "Internet & Artificial Intelligence", "https://careers.google.com",
                "Mountain View, CA / Bangalore, India",
                "Organizing the world's information and making it universally accessible and useful."
        ));

        Company aws = companyRepository.save(new Company(
                "Amazon Web Services", "Cloud Computing & E-Commerce", "https://amazon.jobs",
                "Seattle, WA / Bangalore, India",
                "Earth's most customer-centric company and world's most comprehensive cloud platform."
        ));

        Company gs = companyRepository.save(new Company(
                "Goldman Sachs", "Investment Banking & FinTech", "https://goldmansachs.com/careers",
                "New York, NY / Bangalore, India",
                "Leading global financial institution providing financial and algorithmic advisory services."
        ));

        Company oracle = companyRepository.save(new Company(
                "Oracle", "Enterprise Software & Cloud", "https://oracle.com/careers",
                "Austin, TX / Noida, India",
                "Complete cloud suite of SaaS applications and enterprise Gen2 Cloud infrastructure."
        ));

        Company tcs = companyRepository.save(new Company(
                "TCS Digital", "IT Services & Consulting", "https://tcs.com/careers",
                "Mumbai, India",
                "Global leader in IT consulting and business transformation services."
        ));

        // 4. Seed Recruiter Profiles
        recruiterProfileRepository.save(new RecruiterProfile(recruiterUser1, msft, "Lead University Recruiter", "+91 99887 76655"));
        recruiterProfileRepository.save(new RecruiterProfile(recruiterUser2, google, "Campus Talent Acquisition Partner", "+91 99887 76656"));

        // 5. Seed Job Postings
        JobPosting jobMsft = jobPostingRepository.save(new JobPosting(
                msft, "Software Engineer", "Full Time", "24.5 LPA", 24.5,
                "Bangalore / Hyderabad", 7.5, 0, "CSE, IT, ECE",
                "Java, Spring Boot, Microservices, Distributed Systems, SQL",
                "Join Microsoft Core Engineering to design, develop, and deploy cloud-scale distributed services with high availability and security.",
                LocalDate.now().plusDays(45)
        ));

        JobPosting jobGoogle = jobPostingRepository.save(new JobPosting(
                google, "Cloud Solutions Associate", "Full Time", "32.0 LPA", 32.0,
                "Bangalore", 8.0, 0, "CSE, IT",
                "Python, Distributed Systems, Cloud Architecture, Networking, Kubernetes",
                "Partner with Google Cloud engineering teams to build, scale, and optimize next-generation artificial intelligence and cloud pipelines.",
                LocalDate.now().plusDays(60)
        ));

        JobPosting jobAws = jobPostingRepository.save(new JobPosting(
                aws, "Software Development Engineer - I", "Full Time", "28.0 LPA", 28.0,
                "Hyderabad", 7.5, 0, "CSE, IT, ECE",
                "Java, Data Structures, System Design, AWS, NoSQL",
                "Build foundational distributed systems powering Amazon Prime, DynamoDB, and global retail platforms.",
                LocalDate.now().plusDays(35)
        ));

        JobPosting jobGs = jobPostingRepository.save(new JobPosting(
                gs, "Technology Analyst", "Full Time", "22.0 LPA", 22.0,
                "Bangalore", 7.0, 0, "CSE, IT, ECE",
                "Java, C++, Microservices, High Performance Computing, SQL",
                "Architect ultra-low-latency financial risk engines and algorithmic analytics pipelines.",
                LocalDate.now().plusDays(50)
        ));

        JobPosting jobOracle = jobPostingRepository.save(new JobPosting(
                oracle, "Systems Engineer", "Full Time", "18.5 LPA", 18.5,
                "Noida", 6.5, 0, "CSE, IT, ECE",
                "Java, Linux, Database Internals, Cloud Infrastructure",
                "Contribute to mission-critical Oracle Autonomous Database kernel and cloud automation systems.",
                LocalDate.now().plusDays(25)
        ));

        JobPosting jobTcs = jobPostingRepository.save(new JobPosting(
                tcs, "Member Technical Staff", "Full Time", "9.0 LPA", 9.0,
                "Pune / Mumbai", 6.0, 1, "ALL",
                "Java, Python, Web Technologies, Database Fundamentals",
                "Drive enterprise digital transformation, automated workflow orchestration, and client delivery.",
                LocalDate.now().plusDays(70)
        ));

        // 6. Seed Applications
        Application app1 = new Application(jobMsft, aarav, aiRecommendationService.calculateMatchScore(aarav, jobMsft));
        app1.setStatus("SHORTLISTED");
        app1.setFeedbackNotes("Selected for Technical Round 1 based on stellar CGPA and Spring Boot project credentials.");
        applicationRepository.save(app1);

        Application app2 = new Application(jobGoogle, aarav, aiRecommendationService.calculateMatchScore(aarav, jobGoogle));
        app2.setStatus("APPLIED");
        app2.setFeedbackNotes("Application under initial HR verification.");
        applicationRepository.save(app2);

        Application app3 = new Application(jobAws, aarav, aiRecommendationService.calculateMatchScore(aarav, jobAws));
        app3.setStatus("TECHNICAL_ROUND");
        app3.setFeedbackNotes("Cleared online coding assessment with score 100/100.");
        applicationRepository.save(app3);

        Application app4 = new Application(jobGoogle, priya, aiRecommendationService.calculateMatchScore(priya, jobGoogle));
        app4.setStatus("OFFERED");
        app4.setFeedbackNotes("Outstanding performance in Machine Learning and System Architecture rounds.");
        applicationRepository.save(app4);

        Application app5 = new Application(jobTcs, rohit, aiRecommendationService.calculateMatchScore(rohit, jobTcs));
        app5.setStatus("SHORTLISTED");
        app5.setFeedbackNotes("Shortlisted for digital technical evaluation round.");
        applicationRepository.save(app5);

        // 7. Seed Interview Slots
        interviewSlotRepository.save(new InterviewSlot(
                jobMsft, aarav, "Technical Round 1: DSA & Architecture",
                LocalDateTime.now().plusDays(3).withHour(14).withMinute(0), 45,
                "https://teams.microsoft.com/l/meetup-join/19%3ameeting_msft_crd_041",
                "Panelist: Principal Software Architect. Focus: Graph Algorithms, Concurrency, and REST API design."
        ));

        interviewSlotRepository.save(new InterviewSlot(
                jobAws, aarav, "System Design & Problem Solving",
                LocalDateTime.now().plusDays(6).withHour(11).withMinute(30), 60,
                "https://app.chime.aws/meet/847291-sde1-eval",
                "Panelist: Senior SDE. Prepare scalable cache and database partitioning case studies."
        ));

        // 8. Seed Feedbacks
        feedbackRepository.save(new Feedback(
                app1, aarav, "STUDENT", "Aarav Sharma", "Microsoft Corporation", 5,
                "Excellent interview process. The coding environment was smooth and questions focused on real-world engineering trade-offs."
        ));

        feedbackRepository.save(new Feedback(
                app4, priya, "STUDENT", "Priya Patel", "Google", 5,
                "Insightful technical rounds exploring distributed deep learning frameworks and high-throughput data processing."
        ));

        feedbackRepository.save(new Feedback(
                app1, null, "RECRUITER", "Sarah Jenkins", "Microsoft Campus Team", 5,
                "Strong candidate with solid fundamentals in data structures and enterprise backend frameworks."
        ));

        System.out.println("[DataInitializer] Demo seeding completed successfully. 6 Users, 6 Jobs, 5 Applications, 2 Interviews seeded.");
    }

    private void ensureDiverseCandidatesForDemo() {
        try {
            // Find Microsoft Job or fallback to first job
            JobPosting msftJob = jobPostingRepository.findAll().stream()
                    .filter(j -> j.getCompany() != null && j.getCompany().getName().toLowerCase().contains("microsoft"))
                    .findFirst()
                    .orElse(jobPostingRepository.findAll().stream().findFirst().orElse(null));

            if (msftJob == null) return;

            // 1. Ensure EEE Candidate (Kavita Reddy, Technical Round)
            StudentProfile kavita = studentProfileRepository.findByRollNumber("2022EEE012").orElseGet(() -> {
                User eeeUser = userRepository.findByEmail("kavita@univ.edu").orElseGet(() ->
                        userRepository.save(new User("kavita@univ.edu", "password123", "Kavita Reddy", Role.STUDENT))
                );
                StudentProfile sp = new StudentProfile(
                        eeeUser, "2022EEE012", "Electrical & Electronics Engineering", 2026, 8.75,
                        91.0, 89.5, 0,
                        "Embedded C, IoT, Python, MATLAB, Microcontrollers, VLSI, Circuit Design",
                        "+91 98765 43215"
                );
                sp.setPlacedStatus("IN_PROCESS");
                return studentProfileRepository.save(sp);
            });

            if (applicationRepository.findByJobAndStudent(msftJob, kavita).isEmpty()) {
                Application eeeApp = new Application(msftJob, kavita, 88);
                eeeApp.setStatus("TECHNICAL_ROUND");
                eeeApp.setFeedbackNotes("Cleared technical screening. Recommended for Technical Interview Round 1.");
                applicationRepository.save(eeeApp);
            }

            // 2. Ensure Mechanical Candidate (Vikas Verma, Applied)
            StudentProfile vikas = studentProfileRepository.findByRollNumber("2022ME044").orElseGet(() -> {
                User mechUser = userRepository.findByEmail("vikas@univ.edu").orElseGet(() ->
                        userRepository.save(new User("vikas@univ.edu", "password123", "Vikas Verma", Role.STUDENT))
                );
                StudentProfile sp = new StudentProfile(
                        mechUser, "2022ME044", "Mechanical Engineering", 2026, 8.20,
                        87.0, 84.0, 0,
                        "AutoCAD, SolidWorks, Python, Thermodynamics, Robotics, Finite Element Analysis",
                        "+91 98765 43216"
                );
                sp.setPlacedStatus("SEEKING");
                return studentProfileRepository.save(sp);
            });

            if (applicationRepository.findByJobAndStudent(msftJob, vikas).isEmpty()) {
                Application mechApp = new Application(msftJob, vikas, 79);
                mechApp.setStatus("APPLIED");
                mechApp.setFeedbackNotes("Application under review for Hardware Systems & Automation track.");
                applicationRepository.save(mechApp);
            }

            // 3. Ensure Priya Patel (IT) has Microsoft Application (Offered)
            studentProfileRepository.findByRollNumber("2022IT018").ifPresent(priya -> {
                if (applicationRepository.findByJobAndStudent(msftJob, priya).isEmpty()) {
                    Application itApp = new Application(msftJob, priya, 95);
                    itApp.setStatus("OFFERED");
                    itApp.setFeedbackNotes("Offered Software Engineering position in Cloud & AI group.");
                    applicationRepository.save(itApp);
                }
            });

            // 4. Ensure Rohit Kumar (ECE) has Microsoft Application (Applied)
            studentProfileRepository.findByRollNumber("2022ECE055").ifPresent(rohit -> {
                if (applicationRepository.findByJobAndStudent(msftJob, rohit).isEmpty()) {
                    Application eceApp = new Application(msftJob, rohit, 82);
                    eceApp.setStatus("APPLIED");
                    eceApp.setFeedbackNotes("Applied for Core Engineering drive.");
                    applicationRepository.save(eceApp);
                }
            });

            System.out.println("[DataInitializer] Verified diverse candidates (CSE, IT, ECE, EEE, MECH) for recruiter drives.");
        } catch (Exception e) {
            System.err.println("[DataInitializer] Candidate diversity verification notice: " + e.getMessage());
        }
    }
}
