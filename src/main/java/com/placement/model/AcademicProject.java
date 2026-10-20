package com.placement.model;

public class AcademicProject {
    private String title;
    private String description;
    private String technologies;
    private String repoUrl;

    public AcademicProject() {}

    public AcademicProject(String title, String description, String technologies, String repoUrl) {
        this.title = title;
        this.description = description;
        this.technologies = technologies;
        this.repoUrl = repoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}
