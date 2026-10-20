package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;

    @DBRef
    private Application application;

    @DBRef
    private StudentProfile student;

    private String submittedByRole; // STUDENT, RECRUITER
    private String authorName;
    private String companyOrDrive;
    private Integer rating = 5;
    private String feedbackText;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Feedback() {}

    public Feedback(Application application, StudentProfile student, String submittedByRole, String authorName,
                    String companyOrDrive, Integer rating, String feedbackText) {
        this.application = application;
        this.student = student != null ? student : (application != null ? application.getStudent() : null);
        this.submittedByRole = submittedByRole;
        this.authorName = authorName;
        this.companyOrDrive = companyOrDrive;
        this.rating = rating;
        this.feedbackText = feedbackText;
        this.createdAt = LocalDateTime.now();
    }

    public Feedback(Application application, String submittedByRole, String authorName,
                    String companyOrDrive, Integer rating, String feedbackText) {
        this(application, application != null ? application.getStudent() : null, submittedByRole, authorName, companyOrDrive, rating, feedbackText);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public String getSubmittedByRole() { return submittedByRole; }
    public void setSubmittedByRole(String submittedByRole) { this.submittedByRole = submittedByRole; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCompanyOrDrive() { return companyOrDrive; }
    public void setCompanyOrDrive(String companyOrDrive) { this.companyOrDrive = companyOrDrive; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    public String getCompanyName() { return companyOrDrive; }
    public String getComments() { return feedbackText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
