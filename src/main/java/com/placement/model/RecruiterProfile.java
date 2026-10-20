package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recruiter_profiles")
public class RecruiterProfile {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private Company company;

    private String designation;
    private String phone;

    public RecruiterProfile() {}

    public RecruiterProfile(User user, Company company, String designation, String phone) {
        this.user = user;
        this.company = company;
        this.designation = designation;
        this.phone = phone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
