package org.example.teacheaseapplication.controllers;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.ProfileInformationRequest;
import org.example.teacheaseapplication.dto.requests.UpdatePasswordRequest;
import org.example.teacheaseapplication.dto.responses.PaginatedUsersResponse;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.example.teacheaseapplication.services.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final IUserService userService;

    @PostMapping("/profile")
    public ResponseEntity<HttpStatus> updateUserProfile(@RequestBody ProfileInformationRequest profileInformationRequest, Principal principal  ) {
    return userService.updateUserProfile(profileInformationRequest, principal);
    }
    @PostMapping("/image")
    public ResponseEntity<HttpStatus> uploadProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
        return userService.uploadProfileImage(file, principal);
    }
    @GetMapping("/image")
    public ResponseEntity<byte[]> getProfileImage(Principal principal, @RequestParam String email) {
        return userService.getProfileImage(principal, email);
    }
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Principal principal) {
        return userService.getUserProfile(principal.getName());
    }
    @GetMapping("/profile/{email}")
    public ResponseEntity<UserResponse> getUserProfileByEmail(Principal principal, @PathVariable String email) {
        return userService.getUserProfileByEmail(principal, email);
    }
    @PostMapping("/updatePassword")
    public ResponseEntity<HttpStatus> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest , Principal principal) {
        return userService.updatePassword(updatePasswordRequest, principal);
    }
    @PostMapping("/reset-password")
    @PreAuthorize("permitAll")
    public ResponseEntity<HttpStatus> resetPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, @RequestParam String code) {
        return userService.resetPassword(updatePasswordRequest, code);
    }
    @PutMapping("/set-role/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> setRole(@PathVariable String email, @RequestParam String role) {
        return userService.setRole(email, role);
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedUsersResponse> getUsersPaginated(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword)
    {
        return userService.getUsers(page, size,keyword);
    }

}
