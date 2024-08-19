package org.example.teacheaseapplication.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "posts")
@Builder
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private List<String> files;
}
