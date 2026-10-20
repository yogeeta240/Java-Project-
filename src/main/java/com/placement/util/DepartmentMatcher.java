package com.placement.util;

import com.placement.model.JobPosting;
import com.placement.model.StudentProfile;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Normalizes and equates department representations across the system.
 * Treats short forms (CSE, IT, ECE, EEE/EE, MECH, CIVIL/CE) and full forms
 * (e.g. "Computer Science & Engineering", "Information Technology",
 * "Electronics & Communication Engineering", "Electrical Engineering")
 * as completely identical, while strictly preventing cross-department leaks.
 */
public class DepartmentMatcher {

    private static final Pattern WORD_BOUNDARY_IT = Pattern.compile("(?i)\\bIT\\b");
    private static final Pattern WORD_BOUNDARY_CE = Pattern.compile("(?i)\\bCE\\b");
    private static final Pattern WORD_BOUNDARY_EE = Pattern.compile("(?i)\\bEE\\b");
    private static final Pattern WORD_BOUNDARY_EEE = Pattern.compile("(?i)\\bEEE\\b");
    private static final Pattern WORD_BOUNDARY_ECE = Pattern.compile("(?i)\\bECE\\b");
    private static final Pattern WORD_BOUNDARY_CSE = Pattern.compile("(?i)\\bCSE\\b");
    private static final Pattern WORD_BOUNDARY_CS = Pattern.compile("(?i)\\bCS\\b");
    private static final Pattern WORD_BOUNDARY_MECH = Pattern.compile("(?i)\\bMECH\\b");
    private static final Pattern WORD_BOUNDARY_ME = Pattern.compile("(?i)\\bME\\b");
    private static final Pattern WORD_BOUNDARY_CIVIL = Pattern.compile("(?i)\\bCIVIL\\b");

    private static final Pattern ROLL_CSE = Pattern.compile("(?i)CSE\\d+");
    private static final Pattern ROLL_IT = Pattern.compile("(?i)IT\\d+");
    private static final Pattern ROLL_ECE = Pattern.compile("(?i)ECE\\d+");
    private static final Pattern ROLL_EEE = Pattern.compile("(?i)EEE\\d+|EE\\d+");
    private static final Pattern ROLL_ME = Pattern.compile("(?i)MECH\\d+|ME\\d+");
    private static final Pattern ROLL_CE = Pattern.compile("(?i)CIVIL\\d+|CE\\d+");

    /**
     * Resolves canonical department codes (CSE, IT, ECE, EEE, MECH, CIVIL)
     * from any combination of short forms, full forms, aliases, or roll numbers.
     */
    public static Set<String> getDepartmentCodes(String text) {
        Set<String> codes = new HashSet<>();
        if (text == null || text.isBlank()) {
            return codes;
        }

        String raw = text.toUpperCase();
        String normalized = raw.replace("&", " AND ").replaceAll("[^A-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();

        // 1. CSE: Computer Science & Engineering
        if (normalized.contains("COMPUTER SCIENCE") ||
            normalized.contains("COMPUTER ENG") ||
            normalized.contains("COMP SCI") ||
            WORD_BOUNDARY_CSE.matcher(raw).find() ||
            WORD_BOUNDARY_CS.matcher(raw).find() ||
            ROLL_CSE.matcher(raw).find()) {
            codes.add("CSE");
        }

        // 2. IT: Information Technology
        if (normalized.contains("INFORMATION TECH") ||
            normalized.contains("INFO TECH") ||
            WORD_BOUNDARY_IT.matcher(raw).find() ||
            ROLL_IT.matcher(raw).find()) {
            codes.add("IT");
        }

        // 3. EEE / EE: Electrical Engineering (including Electrical & Electronics Engineering)
        // Must check first because Electrical & Electronics Engineering contains "Electronics"
        boolean isElectrical = normalized.contains("ELECTRICAL") ||
                               WORD_BOUNDARY_EEE.matcher(raw).find() ||
                               WORD_BOUNDARY_EE.matcher(raw).find() ||
                               ROLL_EEE.matcher(raw).find();
        if (isElectrical) {
            codes.add("EEE");
        }

        // 4. ECE: Electronics and Communication Engineering
        if (normalized.contains("COMMUNICATION") ||
            WORD_BOUNDARY_ECE.matcher(raw).find() ||
            ROLL_ECE.matcher(raw).find() ||
            (normalized.contains("ELECTRONIC") && !isElectrical)) {
            codes.add("ECE");
        }

        // 5. MECH: Mechanical Engineering
        if (normalized.contains("MECHANICAL") ||
            WORD_BOUNDARY_MECH.matcher(raw).find() ||
            WORD_BOUNDARY_ME.matcher(raw).find() ||
            ROLL_ME.matcher(raw).find()) {
            codes.add("MECH");
        }

        // 6. CIVIL: Civil Engineering
        if (normalized.contains("CIVIL") ||
            WORD_BOUNDARY_CIVIL.matcher(raw).find() ||
            ROLL_CE.matcher(raw).find() ||
            (WORD_BOUNDARY_CE.matcher(raw).find() && !normalized.contains("COMPUTER"))) {
            codes.add("CIVIL");
        }

        return codes;
    }

    /**
     * Checks if a student's department / roll number matches an allowed branches string or filter.
     */
    public static boolean matches(String studentDept, String rollNumber, String target) {
        if (target == null || target.isBlank()) return true;
        String t = target.toUpperCase().trim();
        if (t.contains("ALL") || t.contains("ANY") || t.contains("OPEN TO ALL")) {
            return true;
        }

        Set<String> studentCodes = getDepartmentCodes(studentDept);
        if (rollNumber != null && !rollNumber.isBlank()) {
            studentCodes.addAll(getDepartmentCodes(rollNumber));
        }

        Set<String> targetCodes = getDepartmentCodes(target);

        // When canonical codes are recognized on both sides:
        // Match ONLY if there is an intersection between canonical codes!
        if (!studentCodes.isEmpty() && !targetCodes.isEmpty()) {
            for (String code : studentCodes) {
                if (targetCodes.contains(code)) {
                    return true;
                }
            }
            return false; // Distinct known departments -> NO MATCH
        }

        // Fallback only if at least one side is an unrecognized custom department (e.g. "Biotechnology")
        if (studentDept != null && !studentDept.isBlank()) {
            String s = studentDept.toUpperCase().trim();
            if (t.equals(s)) return true;
            for (String part : t.split("[,;|/\\s]+")) {
                String p = part.trim();
                // Avoid matching generic stop words like "ENGINEERING", "DEPARTMENT", "AND"
                if (p.length() > 3 && !p.equals("ENGINEERING") && !p.equals("DEPARTMENT") && !p.equals("STUDENT") && !p.equals("TECHNOLOGY")) {
                    if (s.contains(p) || p.contains(s)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean matches(StudentProfile student, String filterOrBranches) {
        if (student == null) return false;
        return matches(student.getDepartment(), student.getRollNumber(), filterOrBranches);
    }

    public static boolean isBranchAllowed(StudentProfile student, JobPosting job) {
        if (student == null || job == null) return false;
        if (job.getAllowedBranches() == null || job.getAllowedBranches().isBlank()) return true;
        return matches(student.getDepartment(), student.getRollNumber(), job.getAllowedBranches());
    }
}
