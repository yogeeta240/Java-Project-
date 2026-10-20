package com.placement.repository;

import com.placement.model.Feedback;
import com.placement.model.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findBySubmittedByRole(String role);
    List<Feedback> findByStudent(StudentProfile student);
}
