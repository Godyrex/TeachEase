package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupResponse {
    private String id;
    private String name;
    private String teacher;
    private List<String> students;
    private List<String> posts;
    private List<String> classSessions;
}
