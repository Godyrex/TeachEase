package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "sessions")
@Builder
public class Session {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDateTime scheduledTime;
    private String url;
    private String location;
    private String group;
    private List<String> presences;
}
