package org.example.teacheaseapplication.repositories;

import org.example.teacheaseapplication.models.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment,String> {
}
