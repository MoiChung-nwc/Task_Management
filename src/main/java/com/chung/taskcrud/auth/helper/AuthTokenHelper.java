package com.chung.taskcrud.auth.helper;

import com.chung.taskcrud.auth.entity.EmailVerificationToken;
import com.chung.taskcrud.auth.entity.RefreshToken;
import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.EmailVerificationTokenRepository;
import com.chung.taskcrud.auth.repository.RefreshTokenRepository;
import com.chung.taskcrud.auth.service.MailService;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AuthTokenHelper {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long VERIFY_TOKEN_TTL_SECONDS = 30 * 60; // 30 minutes

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpSeconds;

    // =========================
    // Verify Token
    // =========================
    public EmailVerificationToken createAndSaveVerifyToken(User user) {
        EmailVerificationToken vt = EmailVerificationToken.builder()
                .user(user)
                .token(generateOpaqueToken())
                .expiresAt(Instant.now().plusSeconds(VERIFY_TOKEN_TTL_SECONDS))
                .build();
        return emailVerificationTokenRepository.save(vt);
    }

    public EmailVerificationToken getVerifyTokenOrThrow(String token) {
        return emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFY_TOKEN_INVALID));
    }

    public void assertVerifyTokenUsable(EmailVerificationToken vt) {
        if (vt.isUsed()) throw new AppException(ErrorCode.VERIFY_TOKEN_USED);
        if (vt.isExpired()) throw new AppException(ErrorCode.VERIFY_TOKEN_EXPIRED);
    }

    public void markVerifyTokenUsed(EmailVerificationToken vt) {
        vt.setUsedAt(Instant.now());
        emailVerificationTokenRepository.save(vt);
    }

    public void sendVerifyEmail(String email, String token) {
        String link = baseUrl + "/api/auth/verify?token=" + token;
        mailService.sendVerifyEmail(email, link);
    }

    // =========================
    // Refresh Token
    // =========================
    public String issueRefreshToken(User user) {
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(generateOpaqueToken())
                .expiresAt(Instant.now().plusSeconds(refreshExpSeconds))
                .build();
        refreshTokenRepository.save(rt);
        return rt.getToken();
    }

    public RefreshToken getRefreshTokenOrThrow(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Invalid refresh token"));
    }

    public void assertRefreshTokenUsable(RefreshToken rt) {
        if (rt.isRevoked() || rt.isExpired()) {
            throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Refresh token expired/revoked");
        }
    }

    public void revokeRefreshToken(RefreshToken rt) {
        rt.setRevokedAt(Instant.now());
        refreshTokenRepository.save(rt);
    }

    public void revokeIfNeeded(RefreshToken rt) {
        if (!rt.isRevoked()) {
            revokeRefreshToken(rt);
        }
    }

    // =========================
    // Utils
    // =========================
    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
