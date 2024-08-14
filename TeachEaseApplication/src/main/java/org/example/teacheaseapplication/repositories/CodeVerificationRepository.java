package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.CodeType;
import org.example.teacheaseapplication.models.CodeVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface CodeVerificationRepository extends MongoRepository<CodeVerification, String> {
    void deleteByEmailAndCodeType(String email, CodeType codeType);
    void deleteAllByExpiryDateBefore(Instant expiryDate);
    CodeVerification findByCode(String code);

}
