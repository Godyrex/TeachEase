package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "groups")
@Builder
public class Group {
    @Id
    private String id;
    private String name;
    private String teacher;
    private List<String> students;
    private List<String> posts;
    private List<String> sessions;
    private List<String> payments;
}
