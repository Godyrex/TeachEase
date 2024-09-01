package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByGroupsContainsAndEmail(String groupId, String email);
}
