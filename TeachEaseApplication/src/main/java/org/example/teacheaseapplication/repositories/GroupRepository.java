package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends MongoRepository<Group,String> {
}
