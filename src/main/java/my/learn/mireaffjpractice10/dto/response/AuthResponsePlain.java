package my.learn.mireaffjpractice10.dto.response;

import my.learn.mireaffjpractice10.model.Token;

public class AuthResponsePlain extends AuthResponse {

    public AuthResponsePlain(Token accessToken) {
        super(accessToken);
    }
}
