package my.learn.mireaffjpractice10.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.TokenType;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class AuthResponse {

    private Token token;

}
