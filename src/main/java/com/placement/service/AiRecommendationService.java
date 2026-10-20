package com.placement.service;

import com.placement.model.JobPosting;
import com.placement.model.StudentProfile;
import com.placement.util.DepartmentMatcher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiRecommendationService {

    /**
     * Calculates an AI compatibility match score (0 - 100%)
     * between student skills/academics and job posting criteria.
     */
    public int calculateMatchScore(StudentProfile student, JobPosting job) {
        if (student == null || job == null) return 70;

        double score = 50.0; // Base baseline

        // 1. CGPA Qualification Evaluation (up to +25 points)
        if (student.getCgpa() != null && job.getMinCgpa() != null) {
            double cgpaDiff = student.getCgpa() - job.getMinCgpa();
            if (cgpaDiff >= 1.5) {
                score += 25;
            } else if (cgpaDiff >= 0.5) {
                score += 20;
            } else if (cgpaDiff >= 0.0) {
                score += 15;
            } else {
                score -= 20; // Ineligible penalty
            }
        }

        // 2. Active Backlogs Check (up to -20 penalty)
        if (student.getActiveBacklogs() != null && job.getMaxBacklogs() != null) {
            if (student.getActiveBacklogs() > job.getMaxBacklogs()) {
                score -= 20;
            }
        }

        // 3. Department / Branch Match (up to +10 points)
        // Treats short forms (CSE, IT, ECE, EEE/EE, MECH, CIVIL) and full forms identically
        if (DepartmentMatcher.isBranchAllowed(student, job)) {
            score += 10;
        }

        // 4. Skills TF-IDF / Keyword Cosine-like Overlap (up to +35 points)
        Set<String> studentSkills = extractKeywords(student.getSkills());
        Set<String> jobSkills = extractKeywords(job.getSkillsRequired() + " " + job.getDescription());

        if (!jobSkills.isEmpty() && !studentSkills.isEmpty()) {
            Set<String> intersection = new HashSet<>(studentSkills);
            intersection.retainAll(jobSkills);

            double jaccardRatio = (double) intersection.size() / (double) Math.min(jobSkills.size(), 8);
            score += Math.min(35.0, jaccardRatio * 40.0);
        }

        // Clamp between 30% and 98% for realistic enterprise AI scoring
        int finalScore = (int) Math.round(score);
        return Math.max(30, Math.min(98, finalScore));
    }

    /**
     * Checks strict eligibility of a student for a job drive
     */
    public boolean isEligible(StudentProfile student, JobPosting job) {
        if (student == null || job == null) return false;

        // CGPA check
        if (student.getCgpa() != null && job.getMinCgpa() != null) {
            if (student.getCgpa() < job.getMinCgpa()) {
                return false;
            }
        }

        // Backlogs check
        if (student.getActiveBacklogs() != null && job.getMaxBacklogs() != null) {
            if (student.getActiveBacklogs() > job.getMaxBacklogs()) {
                return false;
            }
        }

        // Branch check: short forms and full forms of departments are treated identically
        if (!DepartmentMatcher.isBranchAllowed(student, job)) {
            return false;
        }

        return true;
    }

    private Set<String> extractKeywords(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        return Arrays.stream(text.toLowerCase().split("[,;|/\\s]+"))
                .map(String::trim)
                .filter(s -> s.length() > 1)
                .collect(Collectors.toSet());
    }
}
