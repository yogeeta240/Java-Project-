package com.placement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
public class Company {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String industry;
    private String website;
    private String headquarters;
    private String description;
    private boolean verified = true;

    public Company() {}

    public Company(String name, String industry, String website, String headquarters, String description) {
        this.name = name;
        this.industry = industry;
        this.website = website;
        this.headquarters = headquarters;
        this.description = description;
        this.verified = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getHeadquarters() { return headquarters; }
    public void setHeadquarters(String headquarters) { this.headquarters = headquarters; }

    public String getLocation() { return headquarters; }
    public void setLocation(String location) { this.headquarters = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
