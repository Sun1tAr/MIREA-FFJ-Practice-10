package my.learn.mireaffjpractice10.dto.response;

import lombok.Data;
import lombok.Getter;
import my.learn.mireaffjpractice10.model.Token;

@Getter
public class AuthResponse2FA extends AuthResponse {

    private final Token refreshToken;
    public AuthResponse2FA(Token accessToken, Token refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }

}
