package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.requests.PostRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.example.teacheaseapplication.dto.responses.PaginatedPostResponse;
import org.example.teacheaseapplication.dto.responses.PostResponse;
import org.example.teacheaseapplication.models.Group;
import org.example.teacheaseapplication.models.Post;
import org.example.teacheaseapplication.models.Role;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.GroupRepository;
import org.example.teacheaseapplication.repositories.PostRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class GroupServiceImpl implements IGroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final AuthServiceImpl authService;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<GroupResponse> getGroup(String groupId) {
        log.info("Getting group {}", groupId);
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        return new ResponseEntity<>(GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .teacher(group.getTeacher())
                .students(group.getStudents())
                .posts(group.getPosts())
                .sessions(group.getSessions())
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<GroupResponse>> getGroups(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
        if(user.getGroups()!=null)
        {
            List<Group> groups = groupRepository.findAllById(user.getGroups());
            return new ResponseEntity<>(groups.stream().map(group -> GroupResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .build()).toList(), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> createGroup(GroupRequest groupRequest, Principal principal) {
        log.info("Creating group {}", groupRequest.getName());
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
        Group group = Group.builder()
                .name(groupRequest.getName())
                .teacher(principal.getName())
                .students(new ArrayList<>())
                .build();
        groupRepository.save(group);
        user.getGroups().add(group.getId());
        userRepository.save(user);
        addStudentsToGroup(groupRequest.getStudents(), group);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void addStudentsToGroup(List<String> students, Group group) {
        if (group == null) {
            throw new NullPointerException("Group cannot be null");
        }
        log.info(" {} students to add in group {}", students.size(), group.getName());

        for (String student : students) {
            User user = userRepository.findByEmail(student).orElse(null);
            if (user == null) {
                log.info("User {} not found", student);
                log.info("Creating account for user {}", student);
                user = authService.createAccountWithEmail(student);
            }
            if (user.getRole().equals(Role.TEACHER) || user.getRole().equals(Role.ADMIN)) {
                log.info("User {} is a teacher or admin", student);
                continue;
            }
            if (!user.getGroups().contains(group.getId())) {
                log.info("Adding group {} to user {}", group.getName(), student);
                user.getGroups().add(group.getId());
            }
            if (!group.getStudents().contains(student)) {
                log.info("Adding student {} to group {}", student, group.getName());
                group.getStudents().add(student);
            }
            log.info("User {} added to group {}", student, group.getName());
            userRepository.save(user);
        }
        log.info("{} Students in group {}", group.getStudents().size(), group.getName());
        groupRepository.save(group);
    }
    public void removeUsersFromGroup(List<String> students, Group group)
    {
        for(String student: students)
        {
            User user = userRepository.findByEmail(student).orElseThrow(() -> new NoSuchElementException("User not found"));
            user.getGroups().remove(group.getId());
            userRepository.save(user);
        }
    }
    @Override
    public ResponseEntity<HttpStatus> deleteGroup(String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        List<String> users = group.getStudents();
        users.add(group.getTeacher());
        removeUsersFromGroup(users, group);
        groupRepository.deleteById(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> updateGroup(String groupId, GroupRequest groupRequest) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        log.info("Request students {}", groupRequest.getStudents());
        log.info("Group students {}", group.getStudents());
        group.setName(groupRequest.getName().trim());
        List<String> studentsToBeRemoved = new ArrayList<>();
        for (String student : group.getStudents()) {
            if (!groupRequest.getStudents().contains(student)) {
                studentsToBeRemoved.add(student);
                log.info("students loop {}", studentsToBeRemoved);
            }
        }
        log.info("Students to be removed {}", studentsToBeRemoved);
        removeUsersFromGroup(studentsToBeRemoved, group);
        group.getStudents().removeAll(studentsToBeRemoved);
        groupRequest.getStudents().removeAll(group.getStudents());
        log.info("Students to be added {}", groupRequest.getStudents());
        addStudentsToGroup(groupRequest.getStudents(), group);
        groupRepository.save(group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> addStudentToGroup(String groupId, List<String> studentsEmails) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        for (String studentEmail : studentsEmails) {
            User user;
            try {
                if (group.getStudents().contains(studentEmail)) {
                    throw new IllegalArgumentException("Student already in group");
                }
                user = userRepository.findByEmail(studentEmail).orElseThrow(() -> new NoSuchElementException("User not found"));
                if (user.getRole().equals(Role.TEACHER)) {
                    throw new IllegalArgumentException("Teacher cannot be a student");
                }
            } catch (NoSuchElementException e) {
                user = authService.createAccountWithEmail(studentEmail);
            } catch (IllegalArgumentException e) {
                continue;
            }
            group.getStudents().add(user.getEmail());
            user.getGroups().add(group.getId());
            groupRepository.save(group);
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> removeStudentFromGroup(String groupId, String studentEmail) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findByEmail(studentEmail).orElseThrow(() -> new NoSuchElementException("User not found"));
        if(user.getRole().equals(Role.TEACHER)){
            throw new IllegalArgumentException("Teacher cannot be a removed");
        }
        group.getStudents().remove(user.getEmail());
        user.getGroups().remove(group.getId());
        groupRepository.save(group);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> addPost(String groupId, PostRequest postRequest, MultipartFile[] files) {
        log.info("Adding post to course");
        log.info("Course ID: " + groupId);
        log.info("Files: " + Arrays.toString(files));
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        if(group.getPosts()==null){
            group.setPosts(new ArrayList<>());
        }
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .createdAt(LocalDateTime.now())
                .files(files != null ? uploadFiles(files, group) : null)
                .groupId(groupId)
                .build();
        postRepository.save(post);
        group.getPosts().add(post.getId());
        log.info("Post added to group");
        groupRepository.save(group);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    private List<String> uploadFiles(MultipartFile[] files,Group group) {
        log.info("Uploading files");
        String baseDir = "upload" + File.separator + group.getId()+ File.separator;
        return Stream.of(files).map(file -> {
            try {
                File dir = new File(baseDir);
                if (!dir.exists()) {
                    boolean dirsCreated = dir.mkdirs();
                    if (!dirsCreated) {
                        throw new IOException("Failed to create directories");
                    }
                }
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                // Generate a random filename
                String newFileName = UUID.randomUUID() + extension;
                // Define the path to the new file
                String filePath = baseDir + newFileName;
                log.info("File path: " + filePath);
                Files.copy(file.getInputStream(), new File(filePath).toPath());
                log.info("File uploaded");
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
    @Override
    public ResponseEntity<byte[]> downloadFile(String postId, String fileName) {
        log.info("Downloading file {}", fileName);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
            for (String filePath : post.getFiles()) {
                String normalizedFilePath = filePath.replace("\\", "/");
                String extractedFileName = normalizedFilePath.substring(normalizedFilePath.lastIndexOf('/') + 1);
                if (extractedFileName.equals(fileName)) {
                    log.info("File found");
                    try {
                        return ResponseEntity.ok(Files.readAllBytes(new File(normalizedFilePath).toPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<HttpStatus> deletePost(String groupId, String postID) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        Post post = postRepository.findById(postID).orElseThrow(() -> new NoSuchElementException("Post not found"));
        //delete files
        if (post.getFiles() != null) {
            for (String filePath : post.getFiles()) {
                try {
                    Files.delete(new File(filePath).toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        group.getPosts().remove(postID);
        postRepository.deleteById(postID);
        groupRepository.save(group);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponse> getPost(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
        return ResponseEntity.ok(PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .files(post.getFiles())
                .createdAt(post.getCreatedAt())
                .build());
    }

    @Override
    public ResponseEntity<PaginatedPostResponse> getPostsByGroup(String groupId, int page, int size) {
        log.info("page {} , size {}", page,size);
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postsPage = postRepository.findByGroupId(group.getId(), pageable);
        log.info("Posts found: {}", postsPage.getTotalElements());
        log.info("Posts page: {}", postsPage.getNumber());
        log.info("Posts total pages: {}", postsPage.getTotalPages());
        log.info("Posts size: {}", postsPage.getSize());
        return ResponseEntity.ok(PaginatedPostResponse.builder()
                .postResponses(postsPage.getContent().stream().map(post -> PostResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .files(post.getFiles() != null ? returnOnlyFileName(post.getFiles()) : null)
                        .createdAt(post.getCreatedAt())
                        .build()).toList())
                .totalPages(postsPage.getTotalPages())
                .totalElements(postsPage.getTotalElements())
                .currentPage(postsPage.getNumber())
                .build());
    }

    @Override
    public ResponseEntity<HttpStatus> editPost(String groupId, String postId, PostRequest postRequest, MultipartFile[] files,  List<String> filesToBeDeleted) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        if (files != null) {
            if (post.getFiles() != null) {
                post.getFiles().addAll(uploadFiles(files, group));
            }else {
                post.setFiles(uploadFiles(files, group));
            }
        }
        if (filesToBeDeleted != null) {
            if (post.getFiles() != null) {
                for (String fileToBeDeleted : filesToBeDeleted) {
                    for (String filePath : post.getFiles()) {
                        String normalizedFilePath = filePath.replace("\\", "/");
                        String extractedFileName = normalizedFilePath.substring(normalizedFilePath.lastIndexOf('/') + 1);
                        log.info("Extracted file name {}", extractedFileName);
                        log.info("File to be deleted {}", fileToBeDeleted);
                        if (extractedFileName.equals(fileToBeDeleted)) {
                            try {
                                Files.delete(new File(normalizedFilePath).toPath());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            post.getFiles().remove(filePath);
                        }
                    }
                }
            }
        }
        postRepository.save(post);
        return ResponseEntity.ok(HttpStatus.OK);

    }

    public List<String> returnOnlyFileName(List<String> files) {
        log.info("Returning only file name {}", files);
        return files.stream()
                .map(file -> file.replace("\\", "/"))
                .map(file -> file.substring(file.lastIndexOf('/') + 1))
                .toList();
    }
}
