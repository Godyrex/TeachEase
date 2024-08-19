package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "payments")
@Builder
public class Payment {
    @Id
    private String id;
    private String student;
    private String group;
    private double amount;
    private boolean paid;
    private Date creationDate;
    private Date paymentDate;
}
