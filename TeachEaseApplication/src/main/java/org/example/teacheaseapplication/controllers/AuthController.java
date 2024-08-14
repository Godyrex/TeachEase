package org.example.teacheaseapplication.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.teacheaseapplication.dto.requests.LoginRequest;
import org.example.teacheaseapplication.dto.requests.SignupRequest;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.example.teacheaseapplication.services.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@PreAuthorize("permitAll()")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class AuthController {
    private final IAuthService authService;
    @PostMapping("/login")
    ResponseEntity<UserResponse> authenticateUser(@RequestBody LoginRequest loginRequest, @NonNull HttpServletResponse response) {
        return authService.authenticateUser(loginRequest, response);
    }
    @PostMapping("/signup")
    ResponseEntity<HttpStatus> saveUser(@RequestBody SignupRequest signupRequest) {
        return authService.saveUser(signupRequest);
    }
    @GetMapping("/logout")
    ResponseEntity<HttpStatus> logout(Principal principal, HttpServletResponse response, HttpServletRequest request) {
        if(principal != null) {
            return authService.logout(principal.getName(), request, response);
        }
    return authService.logout(request, response);
    }
    @GetMapping("/verify-email")
    ResponseEntity<HttpStatus> verifyEmail(@RequestParam String code) {
        return authService.verifyEmail(code);
    }
    @GetMapping("/resend-verification-email")
    ResponseEntity<HttpStatus> resendVerificationCode(@RequestParam String email) {
        return authService.sendVerificationCode(email);
    }
    @GetMapping("/forgot-password")
    ResponseEntity<HttpStatus> forgotPassword(@RequestParam String email) {
        return authService.sendPasswordResetCode(email);
    }
    @GetMapping("/check-auth")
    ResponseEntity<UserResponse> checkAuth(Principal principal) {
        return authService.checkAuth(principal);
    }

}
