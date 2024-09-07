package org.example.teacheaseapplication.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PresenceRequest {
    private Map<String, Boolean> presences;
}
