package org.example.teacheaseapplication.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.requests.PostRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.example.teacheaseapplication.dto.responses.PaginatedPostResponse;
import org.example.teacheaseapplication.dto.responses.PostResponse;
import org.example.teacheaseapplication.security.CustomAuthorization;
import org.example.teacheaseapplication.services.IGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable String groupId) {
        return groupService.getGroup(groupId);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable String groupId) {
        return groupService.deleteGroup(groupId);
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> updateGroup(@PathVariable String groupId, @RequestBody GroupRequest groupRequest) {
        return groupService.updateGroup(groupId, groupRequest);
    }

    @PutMapping("/{groupId}/add-student")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> addStudentToGroup(@PathVariable String groupId, @RequestBody List<String> studentsEmails) {
        return groupService.addStudentToGroup(groupId, studentsEmails);
    }

    @PutMapping("/{groupId}/remove-student")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> removeStudentFromGroup(@PathVariable String groupId, @RequestBody String studentEmail) {
        return groupService.removeStudentFromGroup(groupId, studentEmail);
    }
    @PutMapping("/{groupId}/leave")
    @PreAuthorize("hasRole('STUDENT')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> leaveGroup(@PathVariable String groupId, Principal principal) {
        return groupService.removeStudentFromGroup(groupId, principal.getName());
    }
    @PutMapping("/{groupId}/addPost")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> addPost(
            @PathVariable String groupId,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "files",required = false) MultipartFile[] files) {
        PostRequest postRequest = PostRequest.builder()
                .title(title)
                .content(content)
                .build();
        return groupService.addPost(groupId, postRequest, files);
    }
    @PreAuthorize("isAuthenticated()&&@customAuthorization.hasPermissionToGroup(#groupId)")
    @GetMapping("/{groupId}/{postId}/{fileName:.+}/download")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable @NotNull String groupId,@PathVariable @NotNull String postId, @PathVariable @NotNull String fileName) {
        return groupService.downloadFile(postId,fileName);
    }
    @DeleteMapping("/{groupId}/deletePost")
    @PreAuthorize("hasRole('TEACHER')&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable String groupId,@RequestParam String postID) {
        return groupService.deletePost(groupId,postID);
    }
    @GetMapping("/{groupId}/post/{postId}")
    @PreAuthorize("isAuthenticated()&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<PostResponse> getPost(@PathVariable String groupId, @PathVariable String postId) {
        return groupService.getPost(postId);
    }
    @GetMapping("/{groupId}/posts")
    @PreAuthorize("isAuthenticated()&&@customAuthorization.hasPermissionToGroup(#groupId)")
    public ResponseEntity<PaginatedPostResponse> getPostsByGroup(
            @PathVariable String groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return groupService.getPostsByGroup(groupId, page, size);
    }
}
