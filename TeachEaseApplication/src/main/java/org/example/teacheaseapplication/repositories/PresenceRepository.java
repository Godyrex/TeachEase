package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Presence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresenceRepository extends MongoRepository<Presence,String> {
    Optional<List<Presence>> findBySession(String sessionId);
    Optional<List<Presence>> findByStudentAndGroupOrderBySessionDateDesc(String studentId, String groupId);
    Optional<Presence> findBySessionAndStudent(String sessionId, String studentId);
    Optional<List<Presence>> findTop5ByStudentAndGroupOrderBySessionDateDesc(String studentId, String groupId);
}
