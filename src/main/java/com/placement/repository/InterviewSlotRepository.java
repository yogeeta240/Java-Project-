package com.placement.repository;

import com.placement.model.InterviewSlot;
import com.placement.model.JobPosting;
import com.placement.model.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewSlotRepository extends MongoRepository<InterviewSlot, String> {
    List<InterviewSlot> findByStudent(StudentProfile student);
    List<InterviewSlot> findByJob(JobPosting job);
    List<InterviewSlot> findByStatus(String status);
    long countByStudentAndStatus(StudentProfile student, String status);
}
