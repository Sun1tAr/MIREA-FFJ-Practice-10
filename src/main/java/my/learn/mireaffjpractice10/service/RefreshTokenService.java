package my.learn.mireaffjpractice10.service;

import my.learn.mireaffjpractice10.model.Token;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    public void saveRefreshToken(Long id, Token token) {
    }

}
