package org.example.teacheaseapplication.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupRequest {
    private String name;
    private List<String> students;
}
