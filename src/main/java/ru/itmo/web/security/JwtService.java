package ru.itmo.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ru.itmo.web.user.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-minutes:15}") long accessTokenMinutes,
            @Value("${jwt.refresh-token-days:7}") long refreshTokenDays
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        this.accessTokenExpiration = accessTokenMinutes * 60 * 1000;
        this.refreshTokenExpiration = refreshTokenDays * 24 * 60 * 60 * 1000;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration, "refresh");
    }

    private String generateToken(User user, long expiration, String type) {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("type", type)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String validateAccessToken(String token) {

        Claims claims = parseToken(token);

        if (!"access".equals(claims.get("type", String.class))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный тип токена"
            );
        }

        return claims.getSubject();
    }

    public String validateRefreshToken(String token) {

        Claims claims = parseToken(token);

        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный тип токена"
            );
        }

        return claims.getSubject();
    }

    private Claims parseToken(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Недействительный или просроченный токен"
            );
        }
    }
}