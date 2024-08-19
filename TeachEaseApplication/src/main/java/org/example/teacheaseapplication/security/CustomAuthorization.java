package org.example.teacheaseapplication.security;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.models.Session;
import org.example.teacheaseapplication.repositories.GroupRepository;
import org.example.teacheaseapplication.repositories.SessionRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CustomAuthorization {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    public boolean hasPermissionToGroup(Principal principal, String groupId) {
            return userRepository.findUserByGroupsContainsAndEmail(groupId, principal.getName());
    }
    public boolean hasPermissionToSession(Principal principal, String sessionId) {
           Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
              return userRepository.findUserByGroupsContainsAndEmail(session.getGroup(), principal.getName());
    }

}
