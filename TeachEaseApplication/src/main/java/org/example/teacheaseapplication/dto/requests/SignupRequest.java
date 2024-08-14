package org.example.teacheaseapplication.dto.requests;

import lombok.Data;

import java.util.Date;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private String lastname;
}
