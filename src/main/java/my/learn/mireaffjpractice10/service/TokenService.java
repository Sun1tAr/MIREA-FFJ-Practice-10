package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.config.JwtConfig;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.TokenPurpose;
import my.learn.mireaffjpractice10.model.TokenType;
import my.learn.mireaffjpractice10.util.JwtTokenUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtConfig jwtConfig;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisTemplate<Long, String> redisTemplate;

    public Token generateAccessToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(
                issuedDate.getTime() + jwtConfig.getAccessLifetime().toMillis()
        );

        String token = jwtTokenUtils.generateToken(userDetails, issuedDate, expiredDate);
        return Token.builder()
                .type(TokenType.BEARER)
                .purpose(TokenPurpose.ACCESS)
                .expiredAt(expiredDate)
                .token(token)
                .build();
    }

    public Token generateRefreshToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(
                issuedDate.getTime() + jwtConfig.getRefreshLifetime().toMillis()
        );

        String payload = jwtTokenUtils.generateToken(userDetails, issuedDate, expiredDate);
        return Token.builder()
                .type(TokenType.BEARER)
                .purpose(TokenPurpose.REFRESH)
                .expiredAt(expiredDate)
                .token(payload)
                .build();
    }

    public void saveRefreshToken(Long id, Token token) {
        redisTemplate.opsForValue().set(id, token.getToken(), jwtConfig.getRefreshLifetime());
    }

    public void deleteRefreshToken(Long id) {
        redisTemplate.opsForValue().getAndDelete(id);

        //todo
        // Перенести логику валидации и потом в контроллере просить новый токен
    }

    private boolean isValidRefreshToken(Long id, String token) {
        String cachedToken = redisTemplate.opsForValue().get(id);

        if (cachedToken == null) {
            throw new AppException("Token ttl configuration is wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return cachedToken.equals(token);
    }


}
