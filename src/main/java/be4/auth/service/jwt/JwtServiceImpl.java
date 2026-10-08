package be4.auth.service.jwt;

import be4.auth.datasource.dao.User;
import be4.auth.service.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey secretKey;

    public JwtServiceImpl(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String generateToken(UUID id, long exp) {
        final long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(id.toString())
                .expiration(new Date(now + exp))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String generateAccessToken(User user) {
        final long expMs = 15 * 60 * 1000; // 15 minutes
        return generateToken(user.getId(), expMs);
    }

    @Override
    public String generateRefreshToken(User user) {
        final long expMs = 12 * 60 * 60 * 1000; // 12 hours
        return generateToken(user.getId(), expMs);
    }

    private JwtParser getJwtParser() {
        return Jwts.parser().verifyWith(secretKey).build();
    }

    @Override
    public boolean validateToken(String token) {
        JwtParser jwt = getJwtParser();

        try {
            jwt.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public UUID getSubject(String token) {
        try {
            JwtParser parser = getJwtParser();
            Jws<Claims> claims = parser.parseSignedClaims(token);
            String sub = claims.getPayload().getSubject();
            return UUID.fromString(sub);
        } catch (RuntimeException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
}
