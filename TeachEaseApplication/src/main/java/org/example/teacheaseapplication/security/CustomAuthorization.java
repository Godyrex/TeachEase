package org.example.teacheaseapplication.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.models.Session;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.SessionRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class CustomAuthorization {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    public String getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    public boolean hasPermissionToGroup( String groupId) {
        log.info("checking permission to group " + groupId);
            return userRepository.existsByGroupsContainsAndEmail(groupId, getUser());
    }
    public boolean hasPermissionToSession( String sessionId) {
           Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
              return userRepository.existsByGroupsContainsAndEmail(session.getGroup(), getUser());
    }

}
