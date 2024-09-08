package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.dto.requests.PresenceRequest;
import org.example.teacheaseapplication.dto.responses.PresenceResponse;
import org.example.teacheaseapplication.models.Presence;
import org.example.teacheaseapplication.models.Session;
import org.example.teacheaseapplication.repositories.PresenceRepository;
import org.example.teacheaseapplication.repositories.SessionRepository;
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
public class PresenceServiceImpl implements IPresenceService {
    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<HttpStatus> createPresences(PresenceRequest presenceRequest, String sessionId) {
        log.info("Creating presences for session {}", sessionId);
        log.info("Presence request: {}", presenceRequest);
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (session.getPresences() == null || session.getPresences().isEmpty()) {
            session.setPresences(new ArrayList<>());
            presenceRequest.getPresences().forEach(studentPresenceRequest -> {
                Presence presence = Presence.builder()
                        .session(sessionId)
                        .group(session.getGroup())
                        .student(studentPresenceRequest.getStudent())
                        .present(studentPresenceRequest.isPresent())
                        .sessionDate(session.getScheduledTime())
                        .build();
                presenceRepository.save(presence);
                session.getPresences().add(presence.getId());
                userRepository.findByEmail(studentPresenceRequest.getStudent()).ifPresent(user -> {
                    if (user.getPresences() == null) {
                        user.setPresences(new ArrayList<>());
                    }
                    user.getPresences().add(presence.getId());
                    userRepository.save(user);
                });
            });
        } else {
            updatePresences(sessionId, presenceRequest);
        }
        sessionRepository.save(session);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PresenceResponse> getPresence(String id) {
        Presence presence = presenceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Presence not found"));
        return ResponseEntity.ok(PresenceResponse.builder()
                .id(presence.getId())
                        .group(presence.getGroup())
                .session(presence.getSession())
                .student(presence.getStudent())
                .present(presence.isPresent())
                        .sessionDate(presence.getSessionDate())
                .build());
    }

    @Override
    public ResponseEntity<List<PresenceResponse>> getPresencesBySession(String sessionId) {
        List<Presence> presences = presenceRepository.findBySession(sessionId).orElseThrow(() -> new NoSuchElementException("Presences not found"));
        return ResponseEntity.ok(presences.stream().map(p -> PresenceResponse.builder()
                .id(p.getId())
                .group(p.getGroup())
                .session(p.getSession())
                .student(p.getStudent())
                .present(p.isPresent())
                .sessionDate(p.getSessionDate())
                .build()).toList());
    }

    @Override
    public ResponseEntity<List<PresenceResponse>> getPresencesByStudentAndGroup(Principal principal, String groupId) {
        List<Presence> presences = presenceRepository.findByStudentAndGroupOrderBySessionDateDesc(principal.getName(), groupId).orElseThrow(() -> new NoSuchElementException("Presences not found"));
        return ResponseEntity.ok(presences.stream().map(p -> PresenceResponse.builder()
                .id(p.getId())
                .group(p.getGroup())
                .session(p.getSession())
                .student(p.getStudent())
                .present(p.isPresent())
                .sessionDate(p.getSessionDate())
                .build()).toList());
    }

    @Override
    public void updatePresences(String sessionId, PresenceRequest presencesRequest) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        presencesRequest.getPresences().forEach(studentPresenceRequest -> {
            Presence presence = presenceRepository.findBySessionAndStudent(sessionId, studentPresenceRequest.getStudent())
                    .orElseGet(() -> Presence.builder()
                            .session(sessionId)
                            .group(session.getGroup())
                            .student(studentPresenceRequest.getStudent())
                            .sessionDate(session.getScheduledTime())
                            .build());
            presence.setPresent(studentPresenceRequest.isPresent());
            presenceRepository.save(presence);
            if (!session.getPresences().contains(presence.getId())) {
                session.getPresences().add(presence.getId());
            }
            userRepository.findByEmail(studentPresenceRequest.getStudent()).ifPresent(user -> {
                if(user.getPresences() == null) {
                    user.setPresences(new ArrayList<>());
                }
                if (!user.getPresences().contains(presence.getId())) {
                    user.getPresences().add(presence.getId());
                }
                userRepository.save(user);
            });
        });
        sessionRepository.save(session);
    }

    @Override
    public ResponseEntity<List<PresenceResponse>> getLatestPresencesByStudentAndGroup(Principal principal, String groupId) {
        List<Presence> presences = presenceRepository.findTop5ByStudentAndGroupOrderBySessionDateDesc(principal.getName(), groupId)
                .orElseThrow(() -> new NoSuchElementException("Presences not found"));
        return ResponseEntity.ok(presences.stream().map(p -> PresenceResponse.builder()
                .id(p.getId())
                .group(p.getGroup())
                .session(p.getSession())
                .student(p.getStudent())
                .present(p.isPresent())
                .sessionDate(p.getSessionDate())
                .build()).toList());
    }
}
