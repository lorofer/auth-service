package be4.auth.service.jwt;

import be4.auth.datasource.dao.User;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateToken(String token);

    UUID getSubject(String token);
}
