package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "job_postings")
public class JobPosting {
    @Id
    private String id;

    @DBRef
    private Company company;

    private String title;
    private String roleType; // Full Time, Internship
    private String packageCtc; // e.g. "24.5 LPA"
    private Double numericCtc; // 24.5 for filtering
    private String location;
    private Double minCgpa = 6.0;
    private Integer maxBacklogs = 0;
    private String allowedBranches; // "CSE, IT, ECE"

    private String skillsRequired; // "Java, Spring Boot, Microservices, SQL"
    private String description;
    private LocalDate deadline;
    private String status = "OPEN"; // OPEN, CLOSED, DRAFT
    private LocalDateTime createdAt = LocalDateTime.now();

    public JobPosting() {}

    public JobPosting(Company company, String title, String roleType, String packageCtc, Double numericCtc,
                      String location, Double minCgpa, Integer maxBacklogs, String allowedBranches,
                      String skillsRequired, String description, LocalDate deadline) {
        this.company = company;
        this.title = title;
        this.roleType = roleType;
        this.packageCtc = packageCtc;
        this.numericCtc = numericCtc;
        this.location = location;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.allowedBranches = allowedBranches;
        this.skillsRequired = skillsRequired;
        this.description = description;
        this.deadline = deadline;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getPackageCtc() { return packageCtc; }
    public void setPackageCtc(String packageCtc) { this.packageCtc = packageCtc; }

    public Double getNumericCtc() { return numericCtc; }
    public void setNumericCtc(Double numericCtc) { this.numericCtc = numericCtc; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }

    public Integer getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(Integer maxBacklogs) { this.maxBacklogs = maxBacklogs; }

    public String getAllowedBranches() { return allowedBranches; }
    public void setAllowedBranches(String allowedBranches) { this.allowedBranches = allowedBranches; }

    public String getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
