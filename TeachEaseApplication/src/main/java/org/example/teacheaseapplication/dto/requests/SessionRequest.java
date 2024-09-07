package org.example.teacheaseapplication.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class SessionRequest {
    private String title;
    private String description;
    private LocalDateTime scheduledTime;
    private String url;
    private String location;
}
