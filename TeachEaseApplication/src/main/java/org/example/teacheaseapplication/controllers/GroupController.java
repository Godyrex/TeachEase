package org.example.teacheaseapplication.controllers;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.example.teacheaseapplication.security.CustomAuthorization;
import org.example.teacheaseapplication.services.IGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@PreAuthorize("isAuthenticated()")
public class GroupController {
    private final IGroupService groupService;
    private final CustomAuthorization customAuthorization;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<HttpStatus> createGroup(@RequestBody GroupRequest groupRequest, Principal principal) {
        return groupService.createGroup(groupRequest, principal);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getGroups(Principal principal) {
        return groupService.getGroups(principal);
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable String groupId) {
        return groupService.getGroup(groupId);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable String groupId) {
        return groupService.deleteGroup(groupId);
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<HttpStatus> updateGroupName(@PathVariable String groupId, @RequestBody String name) {
        return groupService.updateGroupName(groupId, name);
    }

    @PutMapping("/{groupId}/add-student")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<HttpStatus> addStudentToGroup(@PathVariable String groupId, @RequestBody List<String> studentsEmails) {
        return groupService.addStudentToGroup(groupId, studentsEmails);
    }

    @PutMapping("/{groupId}/remove-student")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(principal, #groupId)")
    public ResponseEntity<HttpStatus> removeStudentFromGroup(@PathVariable String groupId, @RequestBody String studentEmail) {
        return groupService.removeStudentFromGroup(groupId, studentEmail);
    }
}
