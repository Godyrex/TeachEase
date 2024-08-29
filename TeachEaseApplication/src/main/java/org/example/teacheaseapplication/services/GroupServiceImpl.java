package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.GroupRequest;
import org.example.teacheaseapplication.dto.responses.GroupResponse;
import org.example.teacheaseapplication.models.Group;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.GroupRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements IGroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

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
        Group group = Group.builder()
                .name(groupRequest.getName())
                .teacher(principal.getName())
                .students(groupRequest.getStudents())
                .build();
        groupRepository.save(group);
        List<String> users = group.getStudents();
        users.add(principal.getName());
        addUsersToGroup(users, group);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    public void addUsersToGroup(List<String> students, Group group)
    {
        for(String student: students)
        {
            User user = userRepository.findByEmail(student).orElseThrow(() -> new NoSuchElementException("User not found"));
            user.getGroups().add(group.getId());
            userRepository.save(user);
        }
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
    public ResponseEntity<HttpStatus> addStudentToGroup(String groupId, String studentEmail) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findByEmail(studentEmail).orElseThrow(() -> new NoSuchElementException("User not found"));
        group.getStudents().add(user.getEmail());
        user.getGroups().add(group.getId());
        groupRepository.save(group);
        userRepository.save(user);
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
