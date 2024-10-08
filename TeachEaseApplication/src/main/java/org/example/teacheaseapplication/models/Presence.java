package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "presences")
@Builder
public class Presence {
    @Id
    private String id;
    private String group;
    private String session;
    private String student;
    private LocalDateTime sessionDate;
    private boolean present;
}
