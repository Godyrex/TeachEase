package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private List<String> files;
    private LocalDateTime createdAt;
}
