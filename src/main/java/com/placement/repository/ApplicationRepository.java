package com.placement.repository;

import com.placement.model.Application;
import com.placement.model.JobPosting;
import com.placement.model.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByStudent(StudentProfile student);
    List<Application> findByJob(JobPosting job);
    Optional<Application> findByJobAndStudent(JobPosting job, StudentProfile student);
    long countByStudentAndStatus(StudentProfile student, String status);
    long countByStudent(StudentProfile student);
    long countByStatus(String status);
}
