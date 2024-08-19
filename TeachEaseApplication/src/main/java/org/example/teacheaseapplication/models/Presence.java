package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "presences")
@Builder
public class Presence {
    @Id
    private String id;
    private String session;
    private String student;
    private boolean present;
}
