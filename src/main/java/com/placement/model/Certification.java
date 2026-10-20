package com.placement.model;

import java.util.UUID;

public class Certification {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String issuingOrganization;
    private String issueDate;
    private String credentialId;
    private byte[] certificateData;
    private String certificateFileName;
    private String certificateContentType;

    public Certification() {}

    public Certification(String title, String issuingOrganization, String issueDate, String credentialId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.credentialId = credentialId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public byte[] getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(byte[] certificateData) {
        this.certificateData = certificateData;
    }

    public String getCertificateFileName() {
        return certificateFileName;
    }

    public void setCertificateFileName(String certificateFileName) {
        this.certificateFileName = certificateFileName;
    }

    public String getCertificateContentType() {
        return certificateContentType;
    }

    public void setCertificateContentType(String certificateContentType) {
        this.certificateContentType = certificateContentType;
    }
}
