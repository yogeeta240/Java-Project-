package com.placement.repository;

import com.placement.model.Company;
import com.placement.model.RecruiterProfile;
import com.placement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends MongoRepository<RecruiterProfile, String> {
    Optional<RecruiterProfile> findByUser(User user);
    List<RecruiterProfile> findByCompany(Company company);
}
