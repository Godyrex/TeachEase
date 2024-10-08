package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post,String> {
    Page<Post> findByGroupId(String groupId, Pageable pageable);
}
