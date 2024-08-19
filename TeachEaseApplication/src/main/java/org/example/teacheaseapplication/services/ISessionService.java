package org.example.teacheaseapplication.services;

import org.example.teacheaseapplication.dto.requests.SessionRequest;
import org.example.teacheaseapplication.dto.responses.SessionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISessionService {
    ResponseEntity<SessionResponse> getSession(String sessionId);
    ResponseEntity<List<SessionResponse>> getSessionByGroupId(String groupId);
    ResponseEntity<HttpStatus> createSession(SessionRequest sessionRequest, String groupId);
    ResponseEntity<HttpStatus> updateSession(String sessionId, SessionRequest sessionRequest);
    ResponseEntity<HttpStatus> deleteSession(String sessionId);

}
