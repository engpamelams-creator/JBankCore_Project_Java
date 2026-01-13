package br.com.jbank.core.infra.defense.token;

import br.com.jbank.core.modulos.usuarios.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret:MySuperSecretKeyForDevelopmentShouldBeChangedInProduction123456}")
    private String secret;

    @Value("${api.security.token.expiration:3600}") 
    private Long expirationSeconds;

    // Utilizamos HMAC256 com tempo de expiração curto para minimizar riscos de sequestro de sessão (session hijacking).
    public String generateToken(User user) {
        return Jwts.builder()
                .setIssuer("JBank Core API")
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(genExpirationDate()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return ""; 
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusSeconds(expirationSeconds).toInstant(ZoneOffset.of("-03:00"));
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
