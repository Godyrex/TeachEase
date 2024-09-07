package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session,String> {
    Optional<List<Session>> findByGroup(String groupId);
    Optional<List<Session>> findByGroupAndScheduledTimeAfter(String groupId, LocalDateTime time);
}
