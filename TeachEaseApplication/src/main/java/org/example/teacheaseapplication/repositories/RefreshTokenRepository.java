package org.example.teacheaseapplication.repositories;


import org.example.teacheaseapplication.models.RefreshToken;
import org.example.teacheaseapplication.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    RefreshToken findByToken(String token);
    void deleteByToken(String token);
    void deleteAllByExpiryDateBefore(Instant instant);
    void deleteAllByUser(User user);
}
