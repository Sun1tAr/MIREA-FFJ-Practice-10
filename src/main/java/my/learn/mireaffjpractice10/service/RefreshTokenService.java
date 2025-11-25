package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.config.JwtConfig;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<Long, String> redisTemplate;
    private final JwtConfig jwtConfig;

    public void saveRefreshToken(Long id, Token token) {
        redisTemplate.opsForValue().set(id, token.getToken(), jwtConfig.getRefreshLifetime());
    }

    public boolean isValidRefreshToken(Long id, String token) {
        String cachedToken = redisTemplate.opsForValue().get(id);

        if (cachedToken == null) {
            throw new AppException("Token ttl configuration is wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return cachedToken.equals(token);
    }

    public void deleteRefreshToken(Long id) {
        redisTemplate.opsForValue().getAndDelete(id);
    }
}
