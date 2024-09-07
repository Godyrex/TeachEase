package org.example.teacheaseapplication.controllers;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.PresenceRequest;
import org.example.teacheaseapplication.dto.responses.PresenceResponse;
import org.example.teacheaseapplication.security.CustomAuthorization;
import org.example.teacheaseapplication.services.IPresenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/presence")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@PreAuthorize("isAuthenticated()")
public class PresenceController {
    private final IPresenceService iPresenceService;
    private final CustomAuthorization customAuthorization;
    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToSession(#sessionId)")
    public ResponseEntity<HttpStatus> createPresences(@RequestBody PresenceRequest presenceRequest, String sessionId) {
        return iPresenceService.createPresences(presenceRequest, sessionId);
    }
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToSession(#sessionId)")
    public ResponseEntity<List<PresenceResponse>> getPresencesBySession(@PathVariable String sessionId) {
        return iPresenceService.getPresencesBySession(sessionId);
    }
    @GetMapping("/student/group/{groupId}")
    @PreAuthorize("@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<List<PresenceResponse>> getPresencesByStudentAndGroup(Principal principal, @PathVariable String groupId) {
        return iPresenceService.getPresencesByStudentAndGroup(principal, groupId);
    }

}
