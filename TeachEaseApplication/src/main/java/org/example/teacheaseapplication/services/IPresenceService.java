package org.example.teacheaseapplication.services;

import org.example.teacheaseapplication.dto.requests.PresenceRequest;
import org.example.teacheaseapplication.dto.responses.PresenceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IPresenceService {
  ResponseEntity<HttpStatus> createPresences(PresenceRequest presenceRequest, String sessionId);
  ResponseEntity<PresenceResponse>  getPresence(String id);
  ResponseEntity<List<PresenceResponse>>   getPresencesBySession(String sessionId);
  ResponseEntity<List<PresenceResponse>>   getPresencesByStudentAndGroup(Principal principal, String groupId);
  void   updatePresences(String id, PresenceRequest presenceRequest);

  ResponseEntity<List<PresenceResponse>> getLatestPresencesByStudentAndGroup(Principal principal, String groupId);
}
