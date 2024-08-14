package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private String lastname;
    private String image;
    private Boolean verified;
    private Boolean ban;
    private String role;
}
