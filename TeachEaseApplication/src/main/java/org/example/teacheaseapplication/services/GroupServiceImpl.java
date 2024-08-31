package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.example.teacheaseapplication.models.Group;
import org.example.teacheaseapplication.models.Role;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.GroupRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class GroupServiceImpl implements IGroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final AuthServiceImpl authService;

    @Override
    public ResponseEntity<GroupResponse> getGroup(String groupId) {
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
        log.info("Added {} students to group {}", group.getStudents().size(), group.getName());
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
    public ResponseEntity<HttpStatus> updateGroupName(String groupId, String name) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        group.setName(name.trim());
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
        group.getStudents().remove(user.getEmail());
        user.getGroups().remove(group.getId());
        groupRepository.save(group);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
