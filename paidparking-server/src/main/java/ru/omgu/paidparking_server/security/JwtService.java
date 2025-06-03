package ru.omgu.paidparking_server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.entity.UserEntity;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long EXPIRATION_MS = 86400000; // 1 день

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getPhoneNumber()) // уникальный идентификатор
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractPhoneNumber(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        String phone = extractPhoneNumber(token);
//        return (phone.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

    public boolean isTokenValid(String token, CustomUserDetails userDetails) {
        try {
            String phone = extractPhoneNumber(token);
            return (phone.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false; // Токен просрочен
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
