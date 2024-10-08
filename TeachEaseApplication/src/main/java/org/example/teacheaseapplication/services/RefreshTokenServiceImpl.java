package org.example.teacheaseapplication.services;

import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.models.RefreshToken;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.RefreshTokenRepository;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final IUserService userService;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, IUserService userService, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean ValidToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken != null && refreshToken.getExpiryDate().isAfter(Instant.now())&&userService.ValidUser(refreshToken.getUser().getEmail());
    }

    @Override
    public User getUserFromToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.getUser();
    }

    @Override
    public RefreshToken createRefreshToken(String email, long expiration) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found")))
                .token(java.util.UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(expiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void deleteUserTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void deleteExpiredTokens() {
        log.info("Deleting expired tokens");
        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
