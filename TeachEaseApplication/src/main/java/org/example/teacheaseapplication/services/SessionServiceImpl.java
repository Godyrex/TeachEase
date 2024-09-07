package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.dto.requests.SessionRequest;
import org.example.teacheaseapplication.dto.responses.SessionResponse;
import org.example.teacheaseapplication.models.Session;
import org.example.teacheaseapplication.repositories.GroupRepository;
import org.example.teacheaseapplication.repositories.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements ISessionService{
    private final SessionRepository sessionRepository;
    private final GroupRepository groupRepository;
    @Override
    public ResponseEntity<SessionResponse> getSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        return ResponseEntity.ok(SessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .scheduledTime(session.getScheduledTime())
                .url(session.getUrl())
                .location(session.getLocation())
                .group(session.getGroup())
                .build());
    }

    @Override
    public ResponseEntity<List<SessionResponse>> getSessionByGroupId(String groupId) {
        List<Session> session = sessionRepository.findByGroup(groupId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        return ResponseEntity.ok(session.stream().map(s -> SessionResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .scheduledTime(s.getScheduledTime())
                .url(s.getUrl())
                .location(s.getLocation())
                .group(s.getGroup())
                .build()).toList());
    }

    @Override
    public ResponseEntity<HttpStatus> createSession(SessionRequest sessionRequest, String groupId) {
        Session session = Session.builder()
                .title(sessionRequest.getTitle())
                .description(sessionRequest.getDescription())
                .scheduledTime(sessionRequest.getScheduledTime())
                .url(sessionRequest.getUrl())
                .location(sessionRequest.getLocation())
                .group(groupId)
                .build();
        sessionRepository.save(session);
        addSessionToGroup(session, groupId);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
    public void addSessionToGroup(Session session, String groupId) {
        groupRepository.findById(groupId).ifPresent(group -> {
            if(group.getSessions() == null) {
                group.setSessions(List.of(session.getId()));
                groupRepository.save(group);
                return;
            }
            if(group.getSessions().contains(session.getId())) {
                return;
            }
            group.getSessions().add(session.getId());
            groupRepository.save(group);
        });
    }
    @Override
    public ResponseEntity<HttpStatus> updateSession(String sessionId, SessionRequest sessionRequest) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        session.setTitle(sessionRequest.getTitle());
        session.setDescription(sessionRequest.getDescription());
        session.setScheduledTime(sessionRequest.getScheduledTime());
        session.setUrl(sessionRequest.getUrl());
        session.setLocation(sessionRequest.getLocation());
        sessionRepository.save(session);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        groupRepository.findById(session.getGroup()).ifPresent(group -> {
            group.getSessions().remove(sessionId);
            groupRepository.save(group);
        });
        sessionRepository.delete(session);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SessionResponse>> getUpcomingSessionsByGroupId(String groupId) {
        List<Session> session = sessionRepository.findByGroupAndScheduledTimeAfter(groupId, LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("Session not found"));
        return ResponseEntity.ok(session.stream().map(s -> SessionResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .scheduledTime(s.getScheduledTime())
                .url(s.getUrl())
                .location(s.getLocation())
                .group(s.getGroup())
                .build()).toList());
    }
}
