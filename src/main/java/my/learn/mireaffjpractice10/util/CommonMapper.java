package my.learn.mireaffjpractice10.util;

import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.dto.response.UserDTO;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonMapper {

    public AuthResponse mapToAuthResponse(Token token) {
        return new AuthResponse(token);
    }

    public UserDTO mapToDTO(User user) {
        throw new AppException("This functional is developing", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
