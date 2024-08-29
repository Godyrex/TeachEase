package org.example.teacheaseapplication.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.teacheaseapplication.dto.requests.LoginRequest;
import org.example.teacheaseapplication.dto.requests.SignupRequest;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface IAuthService {

    ResponseEntity<HttpStatus> logout(String email, HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<HttpStatus> logout( HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<UserResponse> authenticateUser(LoginRequest loginRequest, @NonNull HttpServletResponse response);
    ResponseEntity<HttpStatus> saveUser(SignupRequest signupRequest);
    ResponseEntity<HttpStatus> sendVerificationCode(String email);
    ResponseEntity<HttpStatus> sendPasswordResetCode(String email);
    ResponseEntity<HttpStatus> verifyEmail( String code);
    ResponseEntity<UserResponse> checkAuth(Principal principal);

}
