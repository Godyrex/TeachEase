package org.example.teacheaseapplication.dto.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostRequest {
    private String title;
    private String content;
}
