package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PresenceResponse {
    private String id;
    private String group;
    private String session;
    private String student;
    private LocalDateTime sessionDate;
    private boolean present;
}
