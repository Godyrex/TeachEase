package org.example.teacheaseapplication.services;

import lombok.AllArgsConstructor;
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
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PresenceServiceImpl implements IPresenceService {
    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<HttpStatus> createPresences(PresenceRequest presenceRequest, String sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (session.getPresences() == null || session.getPresences().isEmpty()) {
            presenceRequest.getPresences().forEach((studentEmail, present) -> {
                Presence presence = presenceRepository.save(Presence.builder()
                        .session(sessionId)
                                .group(session.getGroup())
                        .student(studentEmail)
                        .present(present)
                                .sessionDate(session.getScheduledTime())
                        .build());
                if(session.getPresences() == null) {
                    session.setPresences(List.of());
                }
                session.getPresences().add(presence.getId());
                userRepository.findByEmail(studentEmail).ifPresent(user -> {
                    if(user.getPresences() == null) {
                        user.setPresences(List.of());
                    }
                    user.getPresences().add(presence.getId());
                    userRepository.save(user);
                });
            });
        }else{
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
        List<Presence> presences = presenceRepository.findByStudentAndGroup(principal.getName(), groupId).orElseThrow(() -> new NoSuchElementException("Presences not found"));
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
        presencesRequest.getPresences().forEach((studentEmail, present) -> {
            session.getPresences().stream().filter(presenceId -> {
                Presence presence = presenceRepository.findById(presenceId).orElseThrow(() -> new NoSuchElementException("Presence not found"));
                return presence.getStudent().equals(studentEmail);
            }).findFirst().ifPresentOrElse(presenceId -> {
                Presence presence = presenceRepository.findById(presenceId).orElseThrow(() -> new NoSuchElementException("Presence not found"));
                presence.setPresent(present);
                presenceRepository.save(presence);
            }, () -> {
                Presence newPresence = Presence.builder()
                        .session(sessionId)
                        .group(session.getGroup())
                        .student(studentEmail)
                        .present(present)
                        .sessionDate(session.getScheduledTime())
                        .build();
                presenceRepository.save(newPresence);
                session.getPresences().add(newPresence.getId());
                userRepository.findByEmail(studentEmail).ifPresent(user -> {
                    if(user.getPresences() == null) {
                        user.setPresences(List.of());
                    }
                    user.getPresences().add(newPresence.getId());
                    userRepository.save(user);
                });
            });
        });
        sessionRepository.save(session);
    }
}
