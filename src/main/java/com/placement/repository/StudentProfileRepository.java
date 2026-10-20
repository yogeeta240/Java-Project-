package com.placement.repository;

import com.placement.model.StudentProfile;
import com.placement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends MongoRepository<StudentProfile, String> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByRollNumber(String rollNumber);
    List<StudentProfile> findByDepartment(String department);
    long countByPlacedStatus(String placedStatus);
}
