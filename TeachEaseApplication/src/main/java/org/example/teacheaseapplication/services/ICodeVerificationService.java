package org.example.teacheaseapplication.services;


import org.example.teacheaseapplication.models.CodeType;
import org.example.teacheaseapplication.models.CodeVerification;
import org.example.teacheaseapplication.models.Role;

import java.time.Instant;

public interface ICodeVerificationService {
    String generateCode();
    CodeVerification getCodeByCode(String code);
    CodeVerification verifyCode(String codeToVerify);
    CodeVerification saveCode(CodeType codeType, String email, String code, Instant expiryDate);
    CodeVerification saveCode(CodeType codeType, String code, String email, Role role, String institutionID, Instant expiryDate);
    void deleteCode(String email, CodeType codeType);
    void deleteExpiredCodes();
}
