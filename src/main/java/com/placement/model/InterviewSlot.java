package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "interview_slots")
public class InterviewSlot {
    @Id
    private String id;

    @DBRef
    private JobPosting job;

    @DBRef
    private StudentProfile student;

    private String roundName; // Technical Round 1, System Design, HR Discussion
    private LocalDateTime scheduledTime;
    private Integer durationMinutes = 45;
    private String meetingLinkOrVenue;
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED

    private String notes;

    public InterviewSlot() {}

    public InterviewSlot(JobPosting job, StudentProfile student, String roundName,
                         LocalDateTime scheduledTime, Integer durationMinutes,
                         String meetingLinkOrVenue, String notes) {
        this.job = job;
        this.student = student;
        this.roundName = roundName;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.meetingLinkOrVenue = meetingLinkOrVenue;
        this.notes = notes;
        this.status = "SCHEDULED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public JobPosting getJob() { return job; }
    public void setJob(JobPosting job) { this.job = job; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getMeetingLinkOrVenue() { return meetingLinkOrVenue; }
    public void setMeetingLinkOrVenue(String meetingLinkOrVenue) { this.meetingLinkOrVenue = meetingLinkOrVenue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
