package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "student_profiles")
public class StudentProfile {
    @Id
    private String id;

    @DBRef
    private User user;

    @Indexed(unique = true)
    private String rollNumber;
    private String department;
    private Integer batchYear;
    private Double cgpa;
    private Double tenthPercentage;
    private Double twelfthPercentage;
    private Integer activeBacklogs = 0;

    private String skills;
    private String resumeUrl;
    private byte[] resumeData;
    private String resumeFileName;
    private String resumeContentType;
    private String phone;
    private String placedStatus = "UNPLACED"; // UNPLACED, OFFERED, ACCEPTED

    private List<AcademicProject> projects = new ArrayList<>();
    private List<Certification> certifications = new ArrayList<>();

    public StudentProfile() {}

    public StudentProfile(User user, String rollNumber, String department, Integer batchYear, Double cgpa,
                          Double tenthPercentage, Double twelfthPercentage, Integer activeBacklogs,
                          String skills, String phone) {
        this.user = user;
        this.rollNumber = rollNumber;
        this.department = department;
        this.batchYear = batchYear;
        this.cgpa = cgpa;
        this.tenthPercentage = tenthPercentage;
        this.twelfthPercentage = twelfthPercentage;
        this.activeBacklogs = activeBacklogs;
        this.skills = skills;
        this.phone = phone;
        this.placedStatus = "UNPLACED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getBatchYear() { return batchYear; }
    public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Double getTenthPercentage() { return tenthPercentage; }
    public void setTenthPercentage(Double tenthPercentage) { this.tenthPercentage = tenthPercentage; }

    public Double getTwelfthPercentage() { return twelfthPercentage; }
    public void setTwelfthPercentage(Double twelfthPercentage) { this.twelfthPercentage = twelfthPercentage; }

    public Integer getActiveBacklogs() { return activeBacklogs; }
    public void setActiveBacklogs(Integer activeBacklogs) { this.activeBacklogs = activeBacklogs; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public byte[] getResumeData() { return resumeData; }
    public void setResumeData(byte[] resumeData) { this.resumeData = resumeData; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getResumeContentType() { return resumeContentType; }
    public void setResumeContentType(String resumeContentType) { this.resumeContentType = resumeContentType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPlacedStatus() { return placedStatus; }
    public void setPlacedStatus(String placedStatus) { this.placedStatus = placedStatus; }

    public List<AcademicProject> getProjects() {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        return projects;
    }

    public void setProjects(List<AcademicProject> projects) {
        this.projects = projects;
    }

    public List<Certification> getCertifications() {
        if (certifications == null) {
            certifications = new ArrayList<>();
        }
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    public String getAvatarInitials() {
        if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
            String[] parts = user.getFullName().trim().split("\\s+");
            if (parts.length >= 2) {
                return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            }
        }
        return "ST";
    }
}
