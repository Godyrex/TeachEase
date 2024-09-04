package org.example.teacheaseapplication.services;

import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.requests.PostRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface IGroupService {
    ResponseEntity<GroupResponse> getGroup(String groupId);
    ResponseEntity<List<GroupResponse>> getGroups(Principal principal);
    ResponseEntity<HttpStatus> createGroup(GroupRequest groupRequest , Principal principal);
    ResponseEntity<HttpStatus> deleteGroup(String groupId);
    ResponseEntity<HttpStatus> updateGroup(String groupId, GroupRequest groupRequest);
    ResponseEntity<HttpStatus> addStudentToGroup(String groupId, List<String> studentsEmails);
    ResponseEntity<HttpStatus> removeStudentFromGroup(String groupId, String studentEmail);
    ResponseEntity<HttpStatus> addPost(String groupId, PostRequest postRequest, MultipartFile[] files);

    ResponseEntity<byte[]> downloadFile(String groupId, String fileName);

    ResponseEntity<HttpStatus> deletePost(String groupId, String postID);
}
