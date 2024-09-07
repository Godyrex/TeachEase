package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime scheduledTime;
    private String url;
    private String location;
    private String group;
}
