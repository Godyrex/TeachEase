package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
}
