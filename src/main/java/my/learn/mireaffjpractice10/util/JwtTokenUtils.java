package my.learn.mireaffjpractice10.util;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {


    private final SecretKey secretKey;

    public String generateToken(UserDetails userDetails,
                                Date issuedDate,
                                Date expiredDate) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .toList();
        claims.put("ath", authorities);
        claims.put("nme", userDetails.getUsername());
        claims.put("isd", issuedDate.getTime());
        claims.put("exp", expiredDate.getTime());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public List<UserRole> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        List<String> roles = claims.get("ath", List.class);
        List<UserRole> userRoles = new ArrayList<>();
        roles.forEach(role -> userRoles.add(UserRole.valueOf(role)));
        return userRoles;
    }

    private Claims getClaimsFromToken(String token) throws AppException {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
