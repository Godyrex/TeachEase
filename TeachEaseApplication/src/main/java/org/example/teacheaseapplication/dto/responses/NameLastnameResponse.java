package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NameLastnameResponse {
    private String name;
    private String lastname;
}
