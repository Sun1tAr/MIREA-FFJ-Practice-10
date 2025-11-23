package my.learn.mireaffjpractice10.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.config.JwtConfig;
import my.learn.mireaffjpractice10.model.UserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private final JwtConfig jwtConfig;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .toList();
        claims.put("ath", authorities);
        claims.put("nme", userDetails.getUsername());

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtConfig.getLifetime().toMillis());

        claims.put("isd", issuedDate.getTime());
        claims.put("exp", expiredDate.getTime());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public List<UserRoles> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        List<String> roles = claims.get("ath", List.class);
        List<UserRoles> userRoles = new ArrayList<>();
        roles.forEach(role -> userRoles.add(UserRoles.valueOf(role)));
        return userRoles;
    }



    private Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();

    }

}
