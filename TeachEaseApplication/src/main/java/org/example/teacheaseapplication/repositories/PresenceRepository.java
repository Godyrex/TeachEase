package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Presence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenceRepository extends MongoRepository<Presence,String> {
}
