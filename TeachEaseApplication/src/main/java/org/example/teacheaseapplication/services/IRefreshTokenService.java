package org.example.teacheaseapplication.services;


import org.example.teacheaseapplication.models.RefreshToken;
import org.example.teacheaseapplication.models.User;

public interface IRefreshTokenService {
    boolean ValidToken(String token);
    User getUserFromToken(String token);
    RefreshToken createRefreshToken(String email, long expiration);
    void deleteUserTokens(User user);
    void deleteExpiredTokens();
}
