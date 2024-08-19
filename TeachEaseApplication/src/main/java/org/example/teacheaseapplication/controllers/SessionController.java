package org.example.teacheaseapplication.controllers;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.SessionRequest;
import org.example.teacheaseapplication.dto.responses.SessionResponse;
import org.example.teacheaseapplication.security.CustomAuthorization;
import org.example.teacheaseapplication.services.ISessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@PreAuthorize("isAuthenticated()")
public class SessionController {
    private final ISessionService sessionService;
    private final CustomAuthorization customAuthorization;
    @GetMapping("/{sessionId}")
    @PreAuthorize("@customAuthorization.hasPermissionToSession(principal, #sessionId)")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }
    @GetMapping("/group/{groupId}")
    @PreAuthorize("@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<List<SessionResponse>> getSessionByGroupId(@PathVariable String groupId) {
        return sessionService.getSessionByGroupId(groupId);
    }
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<HttpStatus> createSession(@RequestBody SessionRequest sessionRequest, @RequestParam String groupId) {
        return sessionService.createSession(sessionRequest, groupId);
    }
    @PutMapping("/{sessionId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToSession(principal, #sessionId)")
    public ResponseEntity<HttpStatus> updateSession(@PathVariable String sessionId, @RequestBody SessionRequest sessionRequest) {
        return sessionService.updateSession(sessionId, sessionRequest);
    }
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToSession(principal, #sessionId)")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable String sessionId) {
        return sessionService.deleteSession(sessionId);
    }
}
