package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "applications")
public class Application {
    @Id
    private String id;

    @DBRef
    private JobPosting job;

    @DBRef
    private StudentProfile student;

    private LocalDateTime appliedAt = LocalDateTime.now();

    // APPLIED, SHORTLISTED, TECHNICAL_ROUND, HR_ROUND, OFFERED, REJECTED
    private String status = "APPLIED";

    private Integer aiMatchScore = 85;

    private String feedbackNotes;

    public Application() {}

    public Application(JobPosting job, StudentProfile student, Integer aiMatchScore) {
        this.job = job;
        this.student = student;
        this.aiMatchScore = aiMatchScore;
        this.status = "APPLIED";
        this.appliedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public JobPosting getJob() { return job; }
    public void setJob(JobPosting job) { this.job = job; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAiMatchScore() { return aiMatchScore; }
    public void setAiMatchScore(Integer aiMatchScore) { this.aiMatchScore = aiMatchScore; }

    public String getFeedbackNotes() { return feedbackNotes; }
    public void setFeedbackNotes(String feedbackNotes) { this.feedbackNotes = feedbackNotes; }
}
