package org.example.teacheaseapplication.services;

import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IGroupService {
    ResponseEntity<GroupResponse> getGroup(String groupId);
    ResponseEntity<List<GroupResponse>> getGroups(Principal principal);
    ResponseEntity<HttpStatus> createGroup(GroupRequest groupRequest , Principal principal);
    ResponseEntity<HttpStatus> deleteGroup(String groupId);
    ResponseEntity<HttpStatus> updateGroupName(String groupId, String name);
    ResponseEntity<HttpStatus> addStudentToGroup(String groupId, List<String> studentsEmails);
    ResponseEntity<HttpStatus> removeStudentFromGroup(String groupId, String studentEmail);
}
